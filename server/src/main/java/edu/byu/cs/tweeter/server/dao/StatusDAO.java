package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface StatusDAO {

    Pair<List<Status>, Boolean> getPageOfFeed(String targetUserAlias, int pageSize, Long lastTimestamp);

    // TODO: maybe pass in values instead of Status object
    public void addStory(Status status);

    void addFeed(String alias, Status status);

    public void deleteStory(String alias, Long timestamp);
    public Pair<List<Status>, Boolean> getPageOfStories(String targetUserAlias, int pageSize, Long lastTimestamp);

    void deleteFeed(String alias, Long timestamp);
}
