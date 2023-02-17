package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFeedPresenter extends StatusPagePresenter {

    public GetFeedPresenter(PagedView<Status> view, User user) {
        super(view, user);
    }

    @Override
    public void callService() {
        getStatusService().loadMoreItems(getUser(), PAGE_SIZE, getLastItem(), new GetFeedObserver());
    }

    public class GetFeedObserver extends PagedPresenter<Status>.PagedObserver {

        @Override
        public String getPrefix() {
            return "Failed to get feed";
        }
    }
}
