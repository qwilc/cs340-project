package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UpdateFollowResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class UnfollowHandler implements RequestHandler<UnfollowRequest, UpdateFollowResponse> {
    @Override
    public UpdateFollowResponse handleRequest(UnfollowRequest request, Context context) {
        FollowService service = new FollowService();
        return service.unfollow(request);
    }
}