package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.factory.AbstractDAOFactory;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {
    private AbstractDAOFactory daoFactory;

    public StatusService(AbstractDAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public AbstractDAOFactory getDaoFactory() {
        return daoFactory;
    }

    // TODO getFeed and getStory are very similar - could just pass in different DAO, but need base DAO first
    public StatusResponse getFeed(StatusRequest request) {
        validateGetPageRequest(request);

        try {
            long timestamp;
            if(request.getLastStatus() != null) {
                timestamp = request.getLastStatus().getTimestamp();
            }
            else {
                timestamp = -1;
            }
            FeedDAO dao = getDaoFactory().getFeedDAO();
            Pair<List<Status>, Boolean> result = dao.getPageOfFeed(request.getTargetUserAlias(), request.getLimit(), timestamp);
            return new StatusResponse(result.getFirst(), result.getSecond());
        }
        catch(Exception ex) {
            throw new RuntimeException("[Server Error] Failed to get feed: " + ex.getMessage());
        }
    }

    public StatusResponse getStory(StatusRequest request) {
        validateGetPageRequest(request);

        try {
            StoryDAO dao = getDaoFactory().getStoryDAO();

            long timestamp;
            if(request.getLastStatus() != null) {
                timestamp = request.getLastStatus().getTimestamp();
            }
            else {
                timestamp = -1;
            }
            Pair<List<Status>, Boolean> result = dao.getPageOfStories(request.getTargetUserAlias(), request.getLimit(), timestamp);
            return new StatusResponse(result.getFirst(), result.getSecond());
        }
        catch(Exception ex) {
            throw new RuntimeException("[Server Error] Failed to get story: " + ex.getMessage());
        }
    }

    private void validateGetPageRequest(StatusRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an alias");
        }
        else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
    }

    // TODO: The big bad postStatus
    public PostStatusResponse postStatus(PostStatusRequest request) {
        return new PostStatusResponse();
    }
}
