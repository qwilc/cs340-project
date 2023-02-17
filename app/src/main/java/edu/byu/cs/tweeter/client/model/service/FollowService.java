package edu.byu.cs.tweeter.client.model.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.IsFollowerObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public void loadMoreItems(User user, int pageSize, User lastFollowee, PagedObserver<User> observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollowee, new PagedHandler<>(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);
    }

    public void updateFollowingAndFollowers(User user, MainPresenter.GetFollowerCountObserver followerObserver, MainPresenter.GetFolloweeCountObserver followeeObserver) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        getFollowersCount(executor, user, followerObserver);
        getFollowingCount(executor, user, followeeObserver);
    }

    public void getFollowersCount(ExecutorService executor, User user, MainPresenter.GetFollowerCountObserver observer) {
        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetCountHandler(observer));
        executor.execute(followersCountTask);
    }

    public void getFollowingCount(ExecutorService executor, User user, MainPresenter.GetFolloweeCountObserver observer) {
        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetCountHandler(observer));
        executor.execute(followingCountTask);
    }

    public void isFollower(User selectedUser, IsFollowerObserverInterface observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }

    public void follow(User selectedUser, SimpleNotificationObserver observer) {
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new SimpleHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);
    }

    public void unfollow(User selectedUser, SimpleNotificationObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new SimpleHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
    }

}
