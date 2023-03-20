package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowsRequest;
import edu.byu.cs.tweeter.model.net.request.Request;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.util.Pair;

public abstract class PagedTask<T> extends AuthenticatedTask {

    public static final String ITEMS_KEY = "items";
    public static final String MORE_PAGES_KEY = "more-pages";

    /**
     * The user whose items are being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private final User targetUser;

    /**
     * Maximum number of statuses to return (i.e., page size).
     */

    private final int limit;

    /**
     * The last status returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    private final T lastItem;

    /**
     * The items returned in the current page of results.
     */
    private List<T> items;

    /**
     * Indicates whether there are more pages of items that can be retrieved on subsequent calls.
     */
    private boolean hasMorePages;

    protected PagedTask(AuthToken authToken, User targetUser, int limit, T lastItem, Handler messageHandler) {
        super(authToken, messageHandler);
        this.targetUser = targetUser;
        this.limit = limit;
        this.lastItem = lastItem;
    }

    protected User getTargetUser() {
        return targetUser;
    }

    protected int getLimit() {
        return limit;
    }

    protected T getLastItem() {
        return lastItem;
    }


    @Override
    protected void runTask() throws IOException, TweeterRemoteException {
//        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
//        T lastItem = getLastItem() == null ? null : getLastItem();

        Request request = createRequest();
                // new FollowsRequest(getAuthToken(), targetUserAlias, getLimit(), lastFolloweeAlias);
        Response response = callServer(request);
//                getServerFacade().getFollowees(request, FollowService.GET_FOLLOWING_PATH);

        if (response.isSuccess()) {
            extractResponseData(response);
//            setItems(response.getFollowees());
//            setHasMorePages(response.getHasMorePages());
            sendSuccessMessage();
        } else {
            sendFailedMessage(response.getMessage());
        }
    }

    protected void extractResponseData(Response response) {
        setHasMorePages(((PagedResponse)response).getHasMorePages());
    }

    protected abstract Request createRequest();

    protected abstract Response callServer(Request request) throws IOException, TweeterRemoteException;

//    @Override
//    protected /*final*/ void runTask() throws IOException, TweeterRemoteException {
//        Pair<List<T>, Boolean> pageOfItems = getItems();
//
//        items = pageOfItems.getFirst();
//        hasMorePages = pageOfItems.getSecond();
//
//        // Call sendSuccessMessage if successful
//        sendSuccessMessage();
//        // or call sendFailedMessage if not successful
//        // sendFailedMessage()
//    }

    protected abstract Pair<List<T>, Boolean> getItems();

    protected abstract List<User> getUsersForItems(List<T> items);

    @Override
    protected final void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(ITEMS_KEY, (Serializable) items);
        msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }
}
