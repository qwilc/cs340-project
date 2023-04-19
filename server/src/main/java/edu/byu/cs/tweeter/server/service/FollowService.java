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
import edu.byu.cs.tweeter.server.dao.abstractDAO.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.UserDAO;
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
            boolean isValidAuthtoken = getDaoFactory().getAuthtokenDAO().validateAuthtoken(request.getAuthToken().getToken());
            if (!isValidAuthtoken) {
                return new FollowsResponse("Authtoken has expired");
            }

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
            boolean isValidAuthtoken = getDaoFactory().getAuthtokenDAO().validateAuthtoken(request.getAuthToken().getToken());
            if (!isValidAuthtoken) {
                return new FollowsResponse("Authtoken has expired");
            }

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
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }

        AuthToken authtoken = request.getAuthToken();
        String token = authtoken.getToken();
        boolean isValidAuthtoken = getDaoFactory().getAuthtokenDAO().validateAuthtoken(token);
        if (!isValidAuthtoken) {
            return new UpdateFollowResponse("Authtoken has expired");
        }

        AuthtokenDAO authtokenDAO = getDaoFactory().getAuthtokenDAO();
        String alias = authtokenDAO.getAlias(token);
        User user = getDaoFactory().getUserDAO().getUser(alias);

        User followee = request.getFollowee();

        FollowsDAO followsDAO = getDaoFactory().getFollowsDAO();
        UserDAO userDAO = getDaoFactory().getUserDAO();

        if(unfollow) {
            followsDAO.deleteFollow(user.getAlias(), followee.getAlias());
            userDAO.decrementFollowerCount(followee.getAlias());
            userDAO.decrementFolloweeCount(user.getAlias());
        }
        else {
            followsDAO.addFollow(user.getAlias(), user.getFirstName(), user.getLastName(),
                    followee.getAlias(), followee.getFirstName(), followee.getLastName(),
                    user.getImageUrl(), followee.getImageUrl());
            userDAO.incrementFollowerCount(followee.getAlias());
            userDAO.incrementFolloweeCount(user.getAlias());
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
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }

        boolean isValidAuthtoken = getDaoFactory().getAuthtokenDAO().validateAuthtoken(request.getAuthToken().getToken());
        if (!isValidAuthtoken) {
            return new IsFollowerResponse("Authtoken has expired");
        }

        FollowsDAO dao = getDaoFactory().getFollowsDAO();
        System.out.println("follower: " + request.getFollower().getAlias() + " followee: " + request.getFollowee().getAlias());
        boolean isFollower = dao.isFollower(request.getFollower().getAlias(), request.getFollowee().getAlias());
        System.out.println("IsFollower: " + isFollower);
        IsFollowerResponse response = new IsFollowerResponse(isFollower);
        System.out.println("IsFollower from response in Service: " + response.getIsFollower());
        return response;
    }

    public GetCountResponse getFollowingCount(GetCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        }

        boolean isValidAuthtoken = getDaoFactory().getAuthtokenDAO().validateAuthtoken(request.getAuthToken().getToken());
        if (!isValidAuthtoken) {
            return new GetCountResponse("Authtoken has expired");
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
            boolean isValidAuthtoken = getDaoFactory().getAuthtokenDAO().validateAuthtoken(request.getAuthToken().getToken());
            if (!isValidAuthtoken) {
                return new GetCountResponse("Authtoken has expired");
            }

            UserDAO dao = getDaoFactory().getUserDAO();
            String alias = request.getTargetUser().getAlias();
            return new GetCountResponse(dao.getFollowersCount(alias));
        }
        catch(Exception ex) {
            throw new RuntimeException("[Server Error] Failed to get follower count " + ex.getMessage());
        }
    }
}
