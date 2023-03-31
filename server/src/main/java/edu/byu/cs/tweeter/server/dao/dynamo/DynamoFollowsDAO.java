package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataPage;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.dto.FollowBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    @Override
    public void addFollow(String follower_handle, String follower_firstname, String follower_lastname, String followee_handle, String followee_firstname, String followee_lastname) {
        DynamoDbTable<FollowBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowBean.class));

        FollowBean newFollow = new FollowBean();
        newFollow.setFollower_handle(follower_handle);
        newFollow.setFollower_firstname(follower_firstname);
        newFollow.setFollower_lastname(follower_lastname);
        newFollow.setFollowee_handle(followee_handle);
        newFollow.setFollowee_firstname(followee_firstname);
        newFollow.setFollowee_lastname(followee_lastname);
        table.putItem(newFollow);
    }

    public FollowBean getFollow(String follower_handle, String followee_handle) {
        DynamoDbTable<FollowBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowBean.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();

        // TODO: make private (only place using it elsewhere is my bad test)
        return table.getItem(key);
    }

    @Override
    public Boolean isFollower(String follower_alias, String followee_alias) {
        FollowBean follow = getFollow(follower_alias, followee_alias);
        // TODO: Does it return null?
        return follow != null;
    }

    // TODO: delete; currently just keeping it to remind me how to update
//    public void updateNames(String follower_handle, String follower_name, String followee_handle, String followee_name) {
//        DynamoDbTable<FollowBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowBean.class));
//        Key key = Key.builder()
//                .partitionValue(follower_handle).sortValue(followee_handle)
//                .build();
//
//        FollowBean follow = table.getItem(key);
//
//        follow.setFollowee_firstname(followee_name);
//        follow.setFollower_firstname(follower_name);
//        table.updateItem(follow);
//    }

    @Override
    public void deleteFollow(String follower_handle, String followee_handle) {
        DynamoDbTable<FollowBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowBean.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();
        table.deleteItem(key);
    }

    @Override
    public Pair<List<User>, Boolean> getPageOfFollowers(String targetUserAlias, int pageSize, String lastUserAlias ) {
        DynamoDbIndex<FollowBean> index = enhancedClient.table(TableName, TableSchema.fromBean(FollowBean.class)).index(IndexName);
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(true)
                .limit(pageSize);

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
        DynamoDbTable<FollowBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowBean.class));
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
}
