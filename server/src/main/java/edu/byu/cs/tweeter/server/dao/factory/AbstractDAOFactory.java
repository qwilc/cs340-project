package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.abstractDAO.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FeedDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.ImageDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.StoryDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.UserDAO;

public interface AbstractDAOFactory {
    FollowsDAO getFollowsDAO();
    StoryDAO getStoryDAO();
    FeedDAO getFeedDAO();
    AuthtokenDAO getAuthtokenDAO();
    UserDAO getUserDAO();

    ImageDAO getImageDAO();
}
