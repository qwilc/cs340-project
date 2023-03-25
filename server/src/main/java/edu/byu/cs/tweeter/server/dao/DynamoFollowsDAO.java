package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.dto.Follow;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class DynamoFollowsDAO {
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

    public void addFollow(String follower_handle, String follower_name, String followee_handle, String followee_name) {
        DynamoDbTable<Follow> table = enhancedClient.table(TableName, TableSchema.fromBean(Follow.class));

        Follow newFollow = new Follow();
        newFollow.setFollower_handle(follower_handle);
        newFollow.setFollower_name(follower_name);
        newFollow.setFollowee_handle(followee_handle);
        newFollow.setFollowee_name(followee_name);
        table.putItem(newFollow);
    }

    public Follow getFollow(String follower_handle, String followee_handle) {
        DynamoDbTable<Follow> table = enhancedClient.table(TableName, TableSchema.fromBean(Follow.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();

        Follow follow = table.getItem(key);
        return follow;
    }

    public void updateNames(String follower_handle, String follower_name, String followee_handle, String followee_name) {
        DynamoDbTable<Follow> table = enhancedClient.table(TableName, TableSchema.fromBean(Follow.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();

        Follow follow = table.getItem(key);

        //TODO: error in case where it doesn't exist?
        follow.setFollowee_name(followee_name);
        follow.setFollower_name(follower_name);
        table.updateItem(follow);
    }

    public void deleteFollow(String follower_handle, String followee_handle) {
        DynamoDbTable<Follow> table = enhancedClient.table(TableName, TableSchema.fromBean(Follow.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();
        table.deleteItem(key);
    }

    public DataPage<Follow> getPageOfFollowers(String targetUserAlias, int pageSize, String lastUserAlias ) {
        DynamoDbIndex<Follow> index = enhancedClient.table(TableName, TableSchema.fromBean(Follow.class)).index(IndexName);
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

        DataPage<Follow> result = new DataPage<Follow>();

        SdkIterable<Page<Follow>> sdkIterable = index.query(request);
        PageIterable<Follow> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<Follow> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(follow -> result.getValues().add(follow));
                });

        return result;
}

    public DataPage<Follow> getPageOfFollowees(String targetUserAlias, int pageSize, String lastUserAlias ) {
        DynamoDbTable<Follow> table = enhancedClient.table(TableName, TableSchema.fromBean(Follow.class));
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(true)
                .limit(pageSize);

        if(isNonEmptyString(lastUserAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FolloweeAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<Follow> result = new DataPage<Follow>();

        PageIterable<Follow> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<Follow> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(follow -> result.getValues().add(follow));
                });

        return result;
    }
}
