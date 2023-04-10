package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.abstractDAO.UserDAO;
import edu.byu.cs.tweeter.server.dao.dto.UserBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoUserDAO implements UserDAO {
    private static final String TableName = "user";

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private final DynamoDbTable<UserBean> table = enhancedClient.table(TableName, TableSchema.fromBean(UserBean.class));

    private UserBean getUserBean(String alias) {
        Key key = Key.builder().partitionValue(alias).build();
        return table.getItem(key);
    }

    @Override
    public User getUser(String alias) {
        UserBean bean = getUserBean(alias);
        return convertBeanToUser(bean);
    }

    @Override
    public void addUser(String alias, String password, String firstName, String lastName, String imageURL) {
        UserBean bean = new UserBean();
        bean.setAlias(alias);
        bean.setPassword(password);
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

    @Override
    public boolean isAvailableAlias(String alias) {
        UserBean userBean = getUserBean(alias);
        return userBean == null;
    }

    @Override
    public boolean isCorrectPassword(String alias, String password) {
        UserBean userBean = getUserBean(alias);
        return Objects.equals(userBean.getPassword(), password);
    }

    @Override
    public void decrementFollowerCount(String alias) {
        UserBean userBean = getUserBean(alias);
        userBean.setFollowerCount(userBean.getFollowerCount() - 1);
        table.updateItem(userBean);
    }

    @Override
    public void decrementFolloweeCount(String alias) {
        UserBean userBean = getUserBean(alias);
        userBean.setFolloweeCount(userBean.getFolloweeCount() - 1);
        table.updateItem(userBean);
    }

    @Override
    public void incrementFollowerCount(String alias) {
        UserBean userBean = getUserBean(alias);
        userBean.setFollowerCount(userBean.getFollowerCount() + 1);
        table.updateItem(userBean);
    }

    @Override
    public void incrementFolloweeCount(String alias) {
        UserBean userBean = getUserBean(alias);
        userBean.setFolloweeCount(userBean.getFolloweeCount() + 1);
        table.updateItem(userBean);
    }

    @Override
    public String getPasswordHash(String alias) {
        return getUserBean(alias).getPassword();
    }

    public void addUserBatch(List<UserBean> users) {
        List<UserBean> batchToWrite = new ArrayList<>();
        for (UserBean u : users) {
            batchToWrite.add(u);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUserBeans(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfUserBeans(batchToWrite);
        }
    }
    private void writeChunkOfUserBeans(List<UserBean> userDTOs) {
        if(userDTOs.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<UserBean> table = enhancedClient.table(TableName, TableSchema.fromBean(UserBean.class));
        WriteBatch.Builder<UserBean> writeBuilder = WriteBatch.builder(UserBean.class).mappedTableResource(table);
        for (UserBean item : userDTOs) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfUserBeans(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public User convertBeanToUser(UserBean bean) {
        return new User(bean.getFirstName(), bean.getLastName(), bean.getAlias(), bean.getImage_url());
    }
}
