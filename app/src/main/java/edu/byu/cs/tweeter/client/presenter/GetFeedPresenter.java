package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFeedPresenter extends StatusPagePresenter {

    public GetFeedPresenter(PagedView<Status> view, User user) {
        super(view, user);
    }

    @Override
    public void callService() {
        getStatusService().loadMoreFeedItems(getUser(), PAGE_SIZE, getLastItem(), new GetFeedObserver());
    }

    public class GetFeedObserver extends PagedPresenter<Status>.PagedObserver {

        @Override
        public String getPrefix() {
            return "Failed to get feed";
        }
    }
}
