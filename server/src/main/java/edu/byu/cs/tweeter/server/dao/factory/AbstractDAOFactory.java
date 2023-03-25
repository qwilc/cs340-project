package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.FollowsDAO;

public interface AbstractDAOFactory {
    FollowsDAO getFollowsDAO();
}
