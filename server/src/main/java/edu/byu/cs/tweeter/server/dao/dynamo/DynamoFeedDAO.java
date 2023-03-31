package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataPage;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.dto.FeedBean;
import edu.byu.cs.tweeter.server.dao.dto.StoryBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoFeedDAO implements FeedDAO {
    private static final String TableName = "feed";
    public static final String IndexName = "feed_index";

    private static final String AliasAttr = "alias";
    private static final String TimestampAttr = "timestamp";

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    @Override
    public Pair<List<Status>, Boolean> getPageOfFeed(String targetUserAlias, int pageSize, Long lastTimestamp) {
        DynamoDbTable<FeedBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FeedBean.class));
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(true)
                .limit(pageSize);

        if(lastTimestamp != -1) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(AliasAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(TimestampAttr, AttributeValue.builder().n(String.valueOf(lastTimestamp)).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<FeedBean> result = new DataPage<>();

        PageIterable<FeedBean> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<FeedBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(status -> result.getValues().add(status));
                });

        List<Status> statuses = new ArrayList<>();
        for(int i = 0; i < result.getValues().size(); i++) {
            FeedBean bean = result.getValues().get(i);
            statuses.add(convertFeedBeanToStatus(bean));
        }
        return new Pair<>(statuses, result.hasMorePages());
    }

    @Override
    public void addFeed(String alias, Status status) {
        DynamoDbTable<FeedBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FeedBean.class));
        FeedBean storyBean = new FeedBean();
        User user = status.getUser();

        storyBean.setAlias(alias);
        storyBean.setAuthor_alias(user.getAlias());
        storyBean.setTimestamp(status.getTimestamp());
        storyBean.setContent(status.getPost());
        storyBean.setUrls(status.getUrls());
        storyBean.setMentions(status.getMentions());
        storyBean.setFirst_name(user.getFirstName());
        storyBean.setLast_name(user.getLastName());
        storyBean.setImage_url(user.getImageUrl());

        table.putItem(storyBean);
    }

    @Override
    public void deleteFeed(String alias, Long timestamp) {
        DynamoDbTable<StoryBean> table = enhancedClient.table(TableName, TableSchema.fromBean(StoryBean.class));
        Key key = Key.builder()
                .partitionValue(alias).sortValue(timestamp)
                .build();
        table.deleteItem(key);
    }

    private Status convertFeedBeanToStatus(FeedBean bean) {
        User user = new User(bean.getFirst_name(), bean.getLast_name(), bean.getAlias(), bean.getImage_url());
        return new Status(bean.getContent(), user, bean.getTimestamp(), bean.getUrls(), bean.getMentions());
    }
}
