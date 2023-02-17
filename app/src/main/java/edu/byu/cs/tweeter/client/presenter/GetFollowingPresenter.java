package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingPresenter extends UserPagePresenter {

    public interface GetFollowingView extends PagedView<User> {

    }

    public GetFollowingPresenter(PagedView<User> view, User user) {
        super(view, user);
    }

    @Override
    public void callService() {
        getFollowService().loadMoreItems(getUser(), PAGE_SIZE, getLastItem(), new GetFollowingObserver());
    }

    public class GetFollowingObserver extends PagedPresenter<User>.PagedObserver {
        @Override
        public String getPrefix() {
            return "Failed to get following";
        }
    }
}
