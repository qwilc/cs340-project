package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersPresenter extends UserPagePresenter {
    private static final int PAGE_SIZE = 10;

    public interface GetFollowersView extends PagedView<User> {

    }

    public GetFollowersPresenter(GetFollowersView view, User user) {
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
