package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.dto.Follow;

public interface FollowsDAO {
    DataPage<Follow> getPageOfFollowees(String targetUserAlias, int pageSize, String lastUserAlias);

    DataPage<Follow> getPageOfFollowers(String targetUserAlias, int pageSize, String lastUserAlias);

    void addFollow(String follower_handle, String follower_name, String followee_handle, String followee_name);

    void deleteFollow(String follower_handle, String followee_handle);

    Follow getFollow(String follower_handle, String followee_handle);

}
