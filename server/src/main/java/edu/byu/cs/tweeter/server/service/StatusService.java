package edu.byu.cs.tweeter.server.service;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.JsonSerializer;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FeedDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.StoryDAO;
import edu.byu.cs.tweeter.server.dao.dto.FeedBean;
import edu.byu.cs.tweeter.server.dao.factory.AbstractDAOFactory;
import edu.byu.cs.tweeter.server.sqs.SQSAccessor;
import edu.byu.cs.tweeter.server.sqs.UpdateFeedQueueItem;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {
    private AbstractDAOFactory daoFactory;

    public StatusService(AbstractDAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public AbstractDAOFactory getDaoFactory() {
        return daoFactory;
    }

    public StatusResponse getFeed(StatusRequest request) {
        validateGetPageRequest(request);

        boolean isValidAuthtoken = getDaoFactory().getAuthtokenDAO().validateAuthtoken(request.getAuthToken().getToken());
        if (!isValidAuthtoken) {
            return new StatusResponse("Authtoken has expired");
        }

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

        boolean isValidAuthtoken = getDaoFactory().getAuthtokenDAO().validateAuthtoken(request.getAuthToken().getToken());
        if (!isValidAuthtoken) {
            return new StatusResponse("Authtoken has expired");
        }

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
        // TODO: What if status content is empty?
        if(request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an status");
        }
        else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }

        try { // TODO: should there be separate try/catch blocks for each dao call?
            boolean isValidAuthtoken = getDaoFactory().getAuthtokenDAO().validateAuthtoken(request.getAuthToken().getToken());
            if (!isValidAuthtoken) {
                return new PostStatusResponse("Authtoken has expired"); // TODO: Am I handling this right?
            }

            getDaoFactory().getStoryDAO().addStatus(request.getStatus());

            // TODO change the -1 thing in FollowsDAO.getPageOfFollowers
            // TODO should messages have their own objects or am I fine to use what already exists
            // FIXME: Creates a dependency on SQS, ideally would use abstract factory or something
            String messageBody = JsonSerializer.serialize(request.getStatus());
            SQSAccessor.sendPostStatusMessage(messageBody);

            return new PostStatusResponse();
        }
        catch(Exception ex) {
            throw new RuntimeException("[Server Error] Failed to post status: " + ex.getMessage());
        }
    }
}
