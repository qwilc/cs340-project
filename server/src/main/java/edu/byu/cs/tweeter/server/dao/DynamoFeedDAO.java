package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.dto.StatusBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoFeedDAO implements FeedDAO {
    private static final String TableName = "feed";
    public static final String IndexName = "feed_index";

    private static final String AliasAttr = "alias";
    private static final String AuthorAliasAttr = "author_alias";
    private static final String TimestampAttr = "timestamp";
    private static final String URLsAttr = "urls";
    private static final String MentionsAttr = "mentions";

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    public Pair<List<Status>, Boolean> getPageOfFeed(String targetUserAlias, int pageSize, Long lastTimestamp) {
        DynamoDbTable<StatusBean> table = enhancedClient.table(TableName, TableSchema.fromBean(StatusBean.class));
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(true)
                .limit(pageSize);

        if(lastTimestamp != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(AliasAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(TimestampAttr, AttributeValue.builder().n(String.valueOf(lastTimestamp)).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<StatusBean> result = new DataPage<>();

        PageIterable<StatusBean> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<StatusBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(status -> result.getValues().add(status));
                });

        List<Status> statuses = new ArrayList<>();
        for(int i = 0; i < result.getValues().size(); i++) {
            StatusBean status = result.getValues().get(i);
            statuses.add(status.convertToStatus());
        }
        return new Pair<>(statuses, result.hasMorePages());
    }

    // delete? probably at least for testing
    // add
    // getPageofFeed
}
