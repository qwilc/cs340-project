package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public interface AbstractDAOFactory {
    FollowsDAO getFollowsDAO();
    StoryDAO getStoryDAO();
    FeedDAO getFeedDAO();
    AuthtokenDAO getAuthtokenDAO();
    UserDAO getUserDAO();
}
