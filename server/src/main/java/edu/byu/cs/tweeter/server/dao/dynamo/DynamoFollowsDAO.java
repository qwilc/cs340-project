package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataPage;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.dto.FollowBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DynamoFollowsDAO implements FollowsDAO {
    private static final String TableName = "follows";
    public static final String IndexName = "follows_index";

    private static final String FollowerAttr = "follower_handle";
    private static final String FolloweeAttr = "followee_handle";

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private final DynamoDbTable<FollowBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowBean.class));


    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    @Override
    public void addFollow(String follower_handle, String follower_firstname, String follower_lastname,
                          String followee_handle, String followee_firstname, String followee_lastname, String follower_image_url, String followee_image_url) {
        FollowBean newFollow = new FollowBean();
        newFollow.setFollower_handle(follower_handle);
        newFollow.setFollower_firstname(follower_firstname);
        newFollow.setFollower_lastname(follower_lastname);
        newFollow.setFollowee_handle(followee_handle);
        newFollow.setFollowee_firstname(followee_firstname);
        newFollow.setFollowee_lastname(followee_lastname);
        table.putItem(newFollow);
    }

    // TODO: make private (only place using it elsewhere is a badly-done test)
    public FollowBean getFollow(String follower_handle, String followee_handle) {
        System.out.println("In DynamoFollowsDAO.getFollow");
        try {
            System.out.println("follower: " + follower_handle + " followee: " + followee_handle);
            System.out.flush();

            Key key = Key.builder()
                    .partitionValue(follower_handle).sortValue(followee_handle)
                    .build();

            System.out.println("key built");
            System.out.flush();

            return table.getItem(key);
        }
        catch(Exception ex) {
            throw new RuntimeException("Failed to get follow: " + ex.getClass() + ": " + ex.getMessage());
        }
    }

    @Override
    public Boolean isFollower(String follower_alias, String followee_alias) {
        System.out.println("Running DynamoFollowsDAO.isFollower");
        FollowBean follow = getFollow(follower_alias, followee_alias);
        System.out.println("getFollow result returned");
        return follow != null && Objects.equals(follow.getFollower_handle(), follower_alias);
    }

    @Override
    public void deleteFollow(String follower_handle, String followee_handle) {
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();
        table.deleteItem(key);
    }

    /**
     * Gets the users from the database that are following the user specified in the request. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request.
     *
     * @param targetUserAlias the target followee
     * @param pageSize the number of table items to evaluate
     *                 a negative pageSize indicates no limit should be used
     * @return a pair containing a list of followees and a boolean indicating whether there are more pages
     */
    @Override
    public Pair<List<User>, Boolean> getPageOfFollowers(String targetUserAlias, int pageSize, String lastUserAlias ) {
        DynamoDbIndex<FollowBean> index = table.index(IndexName);
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(true);

        if(pageSize > 0) {
            requestBuilder.limit(pageSize); // TODO: apparently this is how many to evaluate, not how many to return
        }

        if(isNonEmptyString(lastUserAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FollowerAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<FollowBean> result = new DataPage<FollowBean>();

        SdkIterable<Page<FollowBean>> sdkIterable = index.query(request);
        PageIterable<FollowBean> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(follow -> result.getValues().add(follow));
                });

        List<User> users = new ArrayList<User>();
        for(int i = 0; i < result.getValues().size(); i++) {
            FollowBean follow = result.getValues().get(i);
            users.add(follow.getFollowerAsUser());
        }
        return new Pair(users, result.hasMorePages());
    }

    @Override
    public Pair<List<User>, Boolean> getPageOfFollowees(String targetUserAlias, int pageSize, String lastUserAlias ) {
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(true) // TODO: make sure both story and feed are in time order
                .limit(pageSize);

        if(isNonEmptyString(lastUserAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FolloweeAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<FollowBean> result = new DataPage<FollowBean>();

        PageIterable<FollowBean> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(follow -> result.getValues().add(follow));
                });

        List<User> users = new ArrayList<User>();
        for(int i = 0; i < result.getValues().size(); i++) {
            FollowBean follow = result.getValues().get(i);
            users.add(follow.getFolloweeAsUser());
        }
        return new Pair<>(users, result.hasMorePages());
    }

    @Override
    public void addFollowersBatch(List<FollowBean> follows) {
        List<FollowBean> batchToWrite = new ArrayList<>();
        for (FollowBean f : follows) {
            batchToWrite.add(f);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfFollowBeans(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfFollowBeans(batchToWrite);
        }
    }
    private void writeChunkOfFollowBeans(List<FollowBean> userDTOs) {
        if(userDTOs.size() > 25)
            throw new RuntimeException("Too many follows to write");

        DynamoDbTable<FollowBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowBean.class));
        WriteBatch.Builder<FollowBean> writeBuilder = WriteBatch.builder(FollowBean.class).mappedTableResource(table);
        for (FollowBean item : userDTOs) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfFollowBeans(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
