package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface StoryDAO {
    public void addStory(Status status);

    public void deleteStory(String alias, Long timestamp);

    public Pair<List<Status>, Boolean> getPageOfStories(String targetUserAlias, int pageSize, Long lastTimestamp);

}
