package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersPresenter extends UserPagePresenter {

    public GetFollowersPresenter(PagedView<User> view, User user) {
        super(view, user);
    }

    @Override
    public void callService() {
        getFollowService().loadMoreItems(getUser(), PAGE_SIZE, getLastItem(), new GetFollowersObserver());
    }

    public class GetFollowersObserver extends PagedPresenter<User>.PagedObserver {
        @Override
        public String getPrefix() {
            return "Failed to get following";
        }
    }
}
