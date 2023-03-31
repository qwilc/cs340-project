package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UpdateFollowRequest;
import edu.byu.cs.tweeter.model.net.request.Request;
import edu.byu.cs.tweeter.model.net.response.Response;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {
    public static final String LOG_TAG = "FollowTask";

    /**
     * The user that is being followed.
     */
    private final User followee;

    public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
    }

    @Override
    protected void extractResponseData(Response response) {

    }

    @Override
    protected Request createRequest() {
        return new UpdateFollowRequest(getAuthToken(), followee);
    }

    @Override
    protected Response callServer(Request request) throws IOException, TweeterRemoteException {
        return getServerFacade().follow((UpdateFollowRequest) request, FollowService.FOLLOW_PATH);
    }
}
