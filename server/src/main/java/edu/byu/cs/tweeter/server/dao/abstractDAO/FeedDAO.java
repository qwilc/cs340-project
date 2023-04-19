package edu.byu.cs.tweeter.server.dao.abstractDAO;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.sqs.UpdateFeedQueueItem;
import edu.byu.cs.tweeter.util.Pair;

public interface FeedDAO {
    Pair<List<Status>, Boolean> getPageOfFeed(String targetUserAlias, int pageSize, Long lastTimestamp);
    void addFeed(String alias, Status status);
    // TODO prolly just need the second addFeed method
    void addFeed(String alias, String author_alias, Long timestamp, String firstName, String lastName, String content, List<String> urls, List<String> mentions, String imageURL);
    public void addFeedBatch(List<UpdateFeedQueueItem> feeds);
    void deleteFeed(String alias, Long timestamp);
}
