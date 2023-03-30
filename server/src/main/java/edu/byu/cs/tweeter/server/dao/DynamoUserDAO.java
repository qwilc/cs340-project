package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetCountRequest;

public class DynamoUserDAO implements UserDAO {
    @Override
    public User getUser(String alias) {
        return null;
    }

    @Override
    public int getFollowingCount(GetCountRequest request) {
        return 0;
    }

    @Override
    public int getFollowersCount(String followee_handle) {
        return 0;
    }
}
