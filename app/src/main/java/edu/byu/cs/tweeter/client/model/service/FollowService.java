package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.IsFollowerObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public static final String GET_FOLLOWING_PATH = "/getfollowing";
    public static final String GET_FOLLOWERS_PATH = "/getfollowers";
    public static final String GET_FOLLOWING_COUNT_PATH = "/getfollowingcount";
    public static final String GET_FOLLOWERS_COUNT_PATH = "/getfollowerscount";
    public static final String IS_FOLLOWER_PATH = "/isfollower";
    public static final String FOLLOW_PATH = "/follow";
    public static final String UNFOLLOW_PATH = "/unfollow";

    public void loadMoreFollowees(User user, int pageSize, User lastFollowee, PagedObserver<User> observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollowee, new PagedHandler<>(observer));
        TaskExecutor.executeTask(getFollowingTask);
    }

    public void loadMoreFollowers(User user, int pageSize, User lastFollower, PagedObserver<User> observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollower, new PagedHandler<>(observer));
        TaskExecutor.executeTask(getFollowersTask);
    }

    public void updateFollowingAndFollowers(User user, MainPresenter.GetFollowerCountObserver followerObserver, MainPresenter.GetFolloweeCountObserver followeeObserver) {
        getFollowersCount(user, followerObserver);
        getFollowingCount(user, followeeObserver);

    }

    public void getFollowersCount(User user, MainPresenter.GetFollowerCountObserver observer) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetCountHandler(observer));
        TaskExecutor.executeTask(followersCountTask);
    }

    public void getFollowingCount(User user, MainPresenter.GetFolloweeCountObserver observer) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetCountHandler(observer));
        TaskExecutor.executeTask(followingCountTask);
    }

    public void isFollower(User selectedUser, IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerHandler(observer));
        TaskExecutor.executeTask(isFollowerTask);
    }

    public void follow(User selectedUser, SimpleNotificationObserver observer) {
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new SimpleHandler(observer));
        TaskExecutor.executeTask(followTask);
    }

    public void unfollow(User selectedUser, SimpleNotificationObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new SimpleHandler(observer));
        TaskExecutor.executeTask(unfollowTask);
    }

}
