package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetCountRequest;

public interface UserDAO {
    User getUser(String alias);

    int getFollowingCount(GetCountRequest request);

    int getFollowersCount(String followee_handle);
}
