package edu.byu.cs.tweeter.server.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FeedDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FollowsDAO;
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
        Status status = new Gson().fromJson(messageBody, Status.class);
        User author = status.getUser();
        String author_alias = author.getAlias();

        FollowsDAO followsDAO = getDaoFactory().getFollowsDAO();

        List<UpdateFeedQueueItem> batch = new ArrayList<>();
        String lastAlias = null;
        boolean hasMorePages = true;
        while(hasMorePages) {
            Pair<List<User>, Boolean> result = followsDAO.getPageOfFollowers(author_alias, 25, lastAlias);
            List<User> followers = result.getFirst();
            hasMorePages = result.getSecond();
            lastAlias = followers.get(followers.size() - 1).getAlias();

            for (User follower : followers) {
                UpdateFeedQueueItem item = new UpdateFeedQueueItem(follower.getAlias(), author_alias, status.getTimestamp(), author.getFirstName(), author.getLastName(), status.getPost(), status.getUrls(), status.getMentions(), author.getImageUrl());
                batch.add(item);
            }

            if(batch.size() >= 25) { // TODO best number here?
                String msg = new Gson().toJson(batch);
                SQSAccessor.sendUpdateFeedMessage(msg);
                batch.clear(); // TODO clear or set to new list?
            }
        }

        if(batch.size() > 0) {
            String msg = new Gson().toJson(batch);
            SQSAccessor.sendUpdateFeedMessage(msg);
        }
    }

    public void updateFeeds(String messageBody) {
        Type listType = new TypeToken<ArrayList<UpdateFeedQueueItem>>(){}.getType();
        List<UpdateFeedQueueItem> items = new Gson().fromJson(messageBody, listType);
        FeedDAO feedDAO = getDaoFactory().getFeedDAO();
        for(UpdateFeedQueueItem item : items) {
            feedDAO.addFeed(item.getAlias(), item.getAuthor_alias(), item.getTimestamp(),
                    item.getFirst_name(), item.getLast_name(), item.getContent(), item.getUrls(),
                    item.getMentions(), item.getImage_url());
        }
    }
}
