package edu.byu.cs.tweeter.server.dao.abstractDAO;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.dto.UserBean;

public interface UserDAO {
    User getUser(String alias);

    void addUser(String alias, String password, String firstName, String lastName, String imageURL);

    int getFollowingCount(String alias);

    int getFollowersCount(String alias);

    boolean isAvailableAlias(String alias);

    boolean isCorrectPassword(String alias, String password);

    void decrementFollowerCount(String alias);

    void decrementFolloweeCount(String alias);

    void incrementFollowerCount(String alias);

    void incrementFolloweeCount(String alias);

    String getPasswordHash(String username);

    void addUserBatch(List<UserBean> users);
}
