package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class UserPagePresenter extends PagedPresenter<User> {

    private FollowService followService;

    public FollowService getFollowService() {
        return followService;
    }

    public UserPagePresenter(PagedView<User> view, User user) {
        super(view, user);
        this.followService = new FollowService();
    }
}
