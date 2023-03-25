package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.DynamoFollowsDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;

public class DynamoDAOFactory implements AbstractDAOFactory {

    @Override
    public FollowsDAO getFollowsDAO() {
        return new DynamoFollowsDAO();
    }
}
