package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataPage;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FeedDAO;
import edu.byu.cs.tweeter.server.dao.dto.FeedBean;
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

    private final DynamoDbTable<FeedBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FeedBean.class));


    @Override
    public Pair<List<Status>, Boolean> getPageOfFeed(String targetUserAlias, int pageSize, Long lastTimestamp) {
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
        FeedBean feedBean = new FeedBean();
        User user = status.getUser();

        feedBean.setAlias(alias);
        feedBean.setAuthor_alias(user.getAlias());
        feedBean.setTimestamp(status.getTimestamp());
        feedBean.setContent(status.getPost());
        feedBean.setUrls(status.getUrls());
        feedBean.setMentions(status.getMentions());
        feedBean.setFirst_name(user.getFirstName());
        feedBean.setLast_name(user.getLastName());
        feedBean.setImage_url(user.getImageUrl());

        table.putItem(feedBean);
    }

    @Override
    public void addFeed(String alias, String author_alias, Long timestamp, String firstName, String lastName, String content, List<String> urls, List<String> mentions, String imageURL) {
        FeedBean feedBean = new FeedBean();

        feedBean.setAlias(alias);
        feedBean.setAuthor_alias(author_alias);
        feedBean.setTimestamp(timestamp);
        feedBean.setContent(content);
        feedBean.setUrls(urls);
        feedBean.setMentions(mentions);
        feedBean.setFirst_name(firstName);
        feedBean.setLast_name(lastName);
        feedBean.setImage_url(imageURL);

        table.putItem(feedBean);
    }

    public void addFeed(FeedBean feedBean) {
        table.putItem(feedBean);
    }

    @Override
    public void deleteFeed(String alias, Long timestamp) {
        Key key = Key.builder()
                .partitionValue(alias).sortValue(timestamp)
                .build();
        table.deleteItem(key);
    }

    private Status convertFeedBeanToStatus(FeedBean bean) {
        User user = new User(bean.getFirst_name(), bean.getLast_name(), bean.getAuthor_alias(), bean.getImage_url());
        return new Status(bean.getContent(), user, bean.getTimestamp(), bean.getUrls(), bean.getMentions());
    }
}
