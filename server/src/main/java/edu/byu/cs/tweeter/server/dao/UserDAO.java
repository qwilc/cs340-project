package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;

public interface UserDAO {
    User getUser(String alias);

    void addUser(String alias, String firstName, String lastName, String imageURL);

    int getFollowingCount(String alias);

    int getFollowersCount(String alias);
}
