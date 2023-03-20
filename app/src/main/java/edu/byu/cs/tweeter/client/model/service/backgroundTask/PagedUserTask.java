package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowsRequest;
import edu.byu.cs.tweeter.model.net.request.Request;
import edu.byu.cs.tweeter.model.net.response.FollowsResponse;
import edu.byu.cs.tweeter.model.net.response.Response;

public abstract class PagedUserTask extends PagedTask<User> {
    protected PagedUserTask(AuthToken authToken, User targetUser, int limit, User lastItem, Handler messageHandler) {
        super(authToken, targetUser, limit, lastItem, messageHandler);
    }

    @Override
    protected Request createRequest() {
        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
        String lastUserAlias = getLastItem() == null ? null : getLastItem().getAlias();
        return new FollowsRequest(getAuthToken(), targetUserAlias, getLimit(), lastUserAlias);
    }

    @Override
    protected void extractResponseData(Response response) {
        super.extractResponseData(response);
        setItems(((FollowsResponse) response).getFollowees());
    }

//    @Override
//    protected void runTask() throws IOException, TweeterRemoteException {
//        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
//        String lastFolloweeAlias = getLastItem() == null ? null : getLastItem().getAlias();
//
//        FollowsRequest request = new FollowsRequest(getAuthToken(), targetUserAlias, getLimit(), lastFolloweeAlias);
//        FollowsResponse response = callServer(request);
////                getServerFacade().getFollowees(request, FollowService.GET_FOLLOWING_PATH);
//
//        if (response.isSuccess()) {
//            setItems(response.getFollowees());
//            setHasMorePages(response.getHasMorePages());
//            sendSuccessMessage();
//        } else {
//            sendFailedMessage(response.getMessage());
//        }
//    }

//    protected abstract FollowsResponse callServer(FollowsRequest request) throws IOException, TweeterRemoteException;

    @Override
    protected final List<User> getUsersForItems(List<User> items) {
        return items;
    }
}
