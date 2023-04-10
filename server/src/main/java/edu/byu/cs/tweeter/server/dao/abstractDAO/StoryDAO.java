package edu.byu.cs.tweeter.server.dao.abstractDAO;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface StoryDAO {
    public void addStatus(Status status);

    public void deleteStatus(String alias, Long timestamp);

    public Pair<List<Status>, Boolean> getPageOfStories(String targetUserAlias, int pageSize, Long lastTimestamp);

}
