package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.dto.AuthtokenBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoAuthtokenDAO implements AuthtokenDAO {

    private static final String TableName = "authtoken";

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    @Override
    public void addAuthtoken(String alias) {
        DynamoDbTable<AuthtokenBean> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenBean.class));
        AuthtokenBean bean = new AuthtokenBean();
        // TODO: ACTUAL TIMESTAMP AND TOKEN
        bean.setTimestamp(123L);
        bean.setAlias(alias);
        bean.setAuthtoken("123");

        table.putItem(bean);
    }

    @Override
    public void removeAuthtoken(String authtoken) {
        DynamoDbTable<AuthtokenBean> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenBean.class));
        Key key = Key.builder()
                .partitionValue(authtoken)
                .build();
        table.deleteItem(key);
    }

    @Override
    public String getAlias(String authtoken) {
        AuthtokenBean bean = getAuthtokenBean(authtoken);
        return bean.getAlias();
    }

    @Override
    public long getTimestamp(String authtoken) {
        AuthtokenBean bean = getAuthtokenBean(authtoken);
        return bean.getTimestamp();
    }

    @Override
    public void updateTimestamp(String authtoken) {
        DynamoDbTable<AuthtokenBean> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenBean.class));
        Key key = Key.builder()
                .partitionValue(authtoken)
                .build();
        AuthtokenBean bean = table.getItem(key);
        // TODO: update timestamp or maybe update in the validate function?
        table.updateItem(bean);

    }

    private AuthtokenBean getAuthtokenBean(String authtoken) {
        DynamoDbTable<AuthtokenBean> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenBean.class));
        Key key = Key.builder()
                .partitionValue(authtoken)
                .build();
        return table.getItem(key);
    }
}
