package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoAuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFeedDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFollowsDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoStoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoUserDAO;

public class DynamoDAOFactory implements AbstractDAOFactory {

    @Override
    public FollowsDAO getFollowsDAO() {
        return new DynamoFollowsDAO();
    }

    @Override
    public StoryDAO getStoryDAO() {
        return new DynamoStoryDAO();
    }

    @Override
    public FeedDAO getFeedDAO() {
        return new DynamoFeedDAO();
    }

    @Override
    public AuthtokenDAO getAuthtokenDAO() {
        return new DynamoAuthtokenDAO();
    }

    @Override
    public UserDAO getUserDAO() {
        return new DynamoUserDAO();
    }
}
