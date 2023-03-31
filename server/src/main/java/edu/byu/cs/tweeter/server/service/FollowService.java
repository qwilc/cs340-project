package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowsRequest;
import edu.byu.cs.tweeter.model.net.request.GetCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UpdateFollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowsResponse;
import edu.byu.cs.tweeter.model.net.response.GetCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UpdateFollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFollowsDAO;
import edu.byu.cs.tweeter.server.dao.factory.AbstractDAOFactory;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {
    private AbstractDAOFactory daoFactory;

    public FollowService(AbstractDAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link DynamoFollowsDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowsResponse getFollowees(FollowsRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        try {
            FollowsDAO dao = getDaoFactory().getFollowsDAO();
            Pair<List<User>, Boolean> result = dao.getPageOfFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());
            return new FollowsResponse(result.getFirst(), result.getSecond());
        }
        catch(Exception ex) {
            throw new RuntimeException("[Server Error] Failed to get following: " + ex.getMessage());
        }
    }

    AbstractDAOFactory getDaoFactory() {
        return daoFactory;
    }

    public FollowsResponse getFollowers(FollowsRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        try {
            DynamoFollowsDAO dao = new DynamoFollowsDAO();
            // TODO: Fix the FollowsRequest attribute names
            Pair<List<User>, Boolean> result = getDaoFactory().getFollowsDAO().getPageOfFollowers(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());
            return new FollowsResponse(result.getFirst(), result.getSecond());
        }
            catch(Exception ex) {
            throw new RuntimeException("[Server Error] Failed to get following: " + ex.getMessage());
        }
    }

    public UpdateFollowResponse processFollowRequest(UpdateFollowRequest request, boolean unfollow) {
        if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        AuthToken authtoken = request.getAuthToken();
        // TODO: set up authtoken validation
        String alias = getDaoFactory().getAuthtokenDAO().getAlias(authtoken.getToken());
        User user = getDaoFactory().getUserDAO().getUser(alias);

        User followee = request.getFollowee();

        FollowsDAO followsDAO = getDaoFactory().getFollowsDAO();

        if(unfollow) {
            followsDAO.deleteFollow(user.getAlias(), followee.getAlias());
        }
        else {
            followsDAO.addFollow(user.getAlias(), user.getFirstName(), user.getLastName(), followee.getAlias(), followee.getFirstName(), followee.getLastName());
        }

        return new UpdateFollowResponse();
    }

    public UpdateFollowResponse follow(UpdateFollowRequest request) {
        return processFollowRequest(request, false);
    }

    public UpdateFollowResponse unfollow(UpdateFollowRequest request) {
        return processFollowRequest(request, true);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        FollowsDAO dao = getDaoFactory().getFollowsDAO();
        boolean isFollower = dao.isFollower(request.getFollower().getAlias(), request.getFollowee().getAlias());
        return new IsFollowerResponse(isFollower);
    }

    public GetCountResponse getFollowingCount(GetCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        }
        UserDAO dao = getDaoFactory().getUserDAO();
        String alias = request.getTargetUser().getAlias();
        return new GetCountResponse(dao.getFollowingCount(alias));
    }

    public GetCountResponse getFollowersCount(GetCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        }
        try {
            UserDAO dao = getDaoFactory().getUserDAO();
            String alias = request.getTargetUser().getAlias();
            return new GetCountResponse(dao.getFollowersCount(alias));
        }
        catch(Exception ex) {
            // TODO: do we throw exception or return failed response or both?
            throw new RuntimeException("[Server Error] Failed to get follower count " + ex.getMessage());
        }
    }
}
