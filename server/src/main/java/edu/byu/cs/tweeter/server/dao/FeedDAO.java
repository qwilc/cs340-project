package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface FeedDAO {
    Pair<List<Status>, Boolean> getPageOfFeed(String targetUserAlias, int pageSize, Long lastTimestamp);

    void addFeed(String alias, Status status);

    void deleteFeed(String alias, Long timestamp);
}
