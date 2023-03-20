package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.Request;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;

public abstract class PagedStatusTask extends PagedTask<Status> {

    protected PagedStatusTask(AuthToken authToken, User targetUser, int limit, Status lastItem, Handler messageHandler) {
        super(authToken, targetUser, limit, lastItem, messageHandler);
    }

    @Override
    protected Request createRequest() {
        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
        return new StatusRequest(getAuthToken(), targetUserAlias, getLimit(), getLastItem());
    }

    @Override
    protected void extractResponseData(Response response) {
        super.extractResponseData(response);
        setItems(((StatusResponse) response).getStatuses());
    }

    @Override
    protected final List<User> getUsersForItems(List<Status> items) {
        return items.stream().map(x -> x.user).collect(Collectors.toList());
    }
}
