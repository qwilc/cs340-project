package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingPresenter extends PagedPresenter {

    public interface GetFollowingView extends PagedView<User> {

    }

    private FollowService followService;

    public GetFollowingPresenter(PagedView<User> view, User user) {
        super(view, user);
        this.followService = new FollowService();
    }

    public void getUser(String userAlias) {
        getUserService().getUser(userAlias, new GetUserObserver());
    }

    @Override
    public void callService() {
        followService.loadMoreItems(getUser(), PAGE_SIZE, ((User) getLastItem()), new PagedPresenter<User>.PagedObserver());
    }
}
