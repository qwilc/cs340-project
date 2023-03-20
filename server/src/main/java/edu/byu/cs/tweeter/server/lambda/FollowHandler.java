package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.model.net.response.UpdateFollowResponse;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.StatusService;

public class FollowHandler implements RequestHandler<FollowRequest, UpdateFollowResponse> {
    @Override
    public UpdateFollowResponse handleRequest(FollowRequest request, Context context) {
        FollowService service = new FollowService();
        return service.follow(request);
    }
}

