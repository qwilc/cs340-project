package edu.byu.cs.tweeter.server.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FeedDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.dto.FeedBean;
import edu.byu.cs.tweeter.server.dao.factory.AbstractDAOFactory;
import edu.byu.cs.tweeter.server.sqs.SQSAccessor;
import edu.byu.cs.tweeter.server.sqs.UpdateFeedQueueItem;
import edu.byu.cs.tweeter.util.Pair;

public class FeedService {
    private final AbstractDAOFactory daoFactory;

    public FeedService(AbstractDAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public AbstractDAOFactory getDaoFactory() {
        return daoFactory;
    }

    public void postUpdateMessages(String messageBody) {
        Status status = JsonSerializer.deserialize(messageBody, Status.class);
        User author = status.getUser();
        String author_alias = author.getAlias();

        FollowsDAO followsDAO = getDaoFactory().getFollowsDAO();

        List<UpdateFeedQueueItem> batch = new ArrayList<>();
        String lastAlias = null;
        boolean hasMorePages = true;

        while (hasMorePages) {
            Pair<List<User>, Boolean> result = followsDAO.getPageOfFollowers(author_alias, 200, lastAlias);
            List<User> followers = result.getFirst();
            hasMorePages = result.getSecond();
            if(followers.size() > 0) {
                lastAlias = followers.get(followers.size() - 1).getAlias();

                for (User follower : followers) {
                    UpdateFeedQueueItem item = new UpdateFeedQueueItem(follower.getAlias(), author_alias, status.getTimestamp(), author.getFirstName(), author.getLastName(), status.getPost(), status.getUrls(), status.getMentions(), author.getImageUrl());
                    batch.add(item);
                }

                if (batch.size() >= 100) { // TODO best number here?
                    String msg = new Gson().toJson(batch);
                    SQSAccessor.sendUpdateFeedMessage(msg);
                    batch.clear(); // TODO clear or set to new list?
                }
            }
        }

        if (batch.size() > 0) {
            String msg = new Gson().toJson(batch);
            SQSAccessor.sendUpdateFeedMessage(msg);
        }
    }

    public void updateFeeds(String messageBody) {
        System.out.println("In FeedService.updateFeeds");
        Type listType = new TypeToken<ArrayList<UpdateFeedQueueItem>>(){}.getType();
        List<UpdateFeedQueueItem> items = new Gson().fromJson(messageBody, listType);
        FeedDAO feedDAO = getDaoFactory().getFeedDAO();
        try {
            feedDAO.addFeedBatch(items);
        }
        catch(Exception ex) {
            throw new RuntimeException("[Server Error] Error updating follower feeds");
        }
    }
}
