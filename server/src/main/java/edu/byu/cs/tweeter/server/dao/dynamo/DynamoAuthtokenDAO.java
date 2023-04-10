package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.abstractDAO.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.dto.AuthtokenBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoAuthtokenDAO implements AuthtokenDAO {

    private static final String TableName = "authtoken";
    private static final long TimeoutLength = 3600000;

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private final DynamoDbTable<AuthtokenBean> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenBean.class));

    @Override
    public boolean validateAuthtoken(String authtoken) {
        if(isValidAuthtoken(authtoken)) {
            updateTimestamp(authtoken);
            return true;
        }
        else {
            removeAuthtoken(authtoken);
            return false;
        }
    }
    @Override
    public boolean isValidAuthtoken(String authtoken) {
            long current_time = System.currentTimeMillis();
            long last_time = getTimestamp(authtoken);

            long elapsed_time = current_time - last_time;

            return elapsed_time < TimeoutLength;
    }

    @Override
    public AuthToken createAuthtoken(String alias) {
        AuthtokenBean bean = new AuthtokenBean();

        long time = System.currentTimeMillis();
        String token = UUID.randomUUID().toString();

        bean.setTimestamp(time);
        bean.setAlias(alias);
        bean.setAuthtoken(token);

        table.putItem(bean);

        return new AuthToken(token, String.valueOf(time));
    }

    @Override
    public void removeAuthtoken(String authtoken) {
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
        AuthtokenBean bean = getAuthtokenBean(authtoken);
        bean.setTimestamp(System.currentTimeMillis());
        table.updateItem(bean);
    }

    private AuthtokenBean getAuthtokenBean(String authtoken) {
        Key key = Key.builder()
                .partitionValue(authtoken)
                .build();
        return table.getItem(key);
    }
}
