package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataPage;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
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

public class DynamoStoryDAO implements StoryDAO {
    private static final String TableName = "story";
    public static final String IndexName = "story_index";

    private static final String AliasAttr = "alias";
    private static final String TimestampAttr = "timestamp";

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    @Override
    public Pair<List<Status>, Boolean> getPageOfStories(String targetUserAlias, int pageSize, Long lastTimestamp) {
        DynamoDbTable<StoryBean> table = enhancedClient.table(TableName, TableSchema.fromBean(StoryBean.class));
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

        DataPage<StoryBean> result = new DataPage<>();

        PageIterable<StoryBean> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<StoryBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(status -> result.getValues().add(status));
                });

        List<Status> statuses = new ArrayList<>();
        for(int i = 0; i < result.getValues().size(); i++) {
            StoryBean bean = result.getValues().get(i);
            statuses.add(convertStoryBeanToStatus(bean));
        }
        return new Pair<>(statuses, result.hasMorePages());
    }


    // TODO: Should we avoid depending on domain models?
    @Override
    public void addStory(Status status) {
        DynamoDbTable<StoryBean> table = enhancedClient.table(TableName, TableSchema.fromBean(StoryBean.class));
        StoryBean storyBean = new StoryBean();
        User user = status.getUser();

        storyBean.setAlias(user.getAlias());
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
    public void deleteStory(String alias, Long timestamp) {
        DynamoDbTable<StoryBean> table = enhancedClient.table(TableName, TableSchema.fromBean(StoryBean.class));
        Key key = Key.builder()
                .partitionValue(alias).sortValue(timestamp)
                .build();
        table.deleteItem(key);
    }

    private Status convertStoryBeanToStatus(StoryBean bean) {
        User user = new User(bean.getFirst_name(), bean.getLast_name(), bean.getAlias(), bean.getImage_url());
        return new Status(bean.getContent(), user, bean.getTimestamp(), bean.getUrls(), bean.getMentions());
    }
}
