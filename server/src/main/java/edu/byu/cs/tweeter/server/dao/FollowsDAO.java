package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.dto.FollowBean;
import edu.byu.cs.tweeter.util.Pair;

public interface FollowsDAO { // TODO: minor, replace handle with alias
    Pair<List<User>, Boolean> getPageOfFollowees(String targetUserAlias, int pageSize, String lastUserAlias);

    Pair<List<User>, Boolean> getPageOfFollowers(String targetUserAlias, int pageSize, String lastUserAlias);

    void addFollow(String follower_handle, String follower_firstname, String follower_lastname, String followee_handle, String followee_firstname, String followee_lastname);

    void deleteFollow(String follower_handle, String followee_handle);

    FollowBean getFollow(String follower_handle, String followee_handle);

    Boolean isFollower(String follower_alias, String followee_alias);

}
