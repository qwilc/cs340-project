package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dto.UserBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoUserDAO implements UserDAO {
    private static final String TableName = "user";

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private UserBean getUserBean(String alias) {
        DynamoDbTable<UserBean> table = enhancedClient.table(TableName, TableSchema.fromBean(UserBean.class));
        Key key = Key.builder().partitionValue(alias).build();
        return table.getItem(key);
    }

    @Override
    public User getUser(String alias) {
        UserBean bean = getUserBean(alias);
        return convertBeanToUser(bean);
    }

    @Override
    public void addUser(String alias, String firstName, String lastName, String imageURL) {
        DynamoDbTable<UserBean> table = enhancedClient.table(TableName, TableSchema.fromBean(UserBean.class));
        UserBean bean = new UserBean();
        bean.setAlias(alias);
        bean.setFirstName(firstName);
        bean.setLastName(lastName);
        bean.setImage_url(imageURL);
        bean.setFolloweeCount(0);
        bean.setFollowerCount(0);
        table.putItem(bean);
    }

    @Override
    public int getFollowingCount(String alias) {
        UserBean bean = getUserBean(alias);
        return bean.getFolloweeCount();
    }

    @Override
    public int getFollowersCount(String alias) {
        UserBean bean = getUserBean(alias);
        return bean.getFollowerCount();
    }

    private User convertBeanToUser(UserBean bean) {
        return new User(bean.getFirstName(), bean.getLastName(), bean.getAlias(), bean.getImage_url());
    }
}
