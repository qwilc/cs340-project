package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetStoryPresenter extends StatusPagePresenter {

    public GetStoryPresenter(PagedView<Status> view, User user) {
        super(view, user);
    }

    @Override
    public void callService() {
        getStatusService().loadMoreStoryItems(getUser(), PAGE_SIZE, getLastItem(), new GetStoryObserver());
    }

    public class GetStoryObserver extends PagedPresenter<Status>.PagedObserver {

        @Override
        public String getPrefix() {
            return "Failed to get story";
        }
    }

}
