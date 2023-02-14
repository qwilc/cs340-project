package edu.byu.cs.tweeter.client.model.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.FollowHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.UnfollowHandler;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public interface GetFollowObserver {

        void displayError(String message);

        void displayException(Exception ex);

        void addItems(List<User> items, boolean hasMorePages);
    }

    public interface GetCountObserver {

        void displayMessage(String message);

        void displayFollowerCount(int count);

        void displayFolloweeCount(int count);
    }

    public interface IsFollowerObserver {
        void displayMessage(String message);

        void displayFollowButton(boolean isFollower);
    }

    public interface FollowObserver {
        void displayMessage(String message);

        void updateFollowButton(boolean b);

        void setFollowButtonEnabled(boolean b);

        void updateFollowingAndFollowers();
    }

    public interface UnfollowObserver {
        void displayMessage(String message);

        void updateFollowButton(boolean b);

        void setFollowButtonEnabled(boolean b);

        void updateFollowingAndFollowers();
    }

    public void loadMoreItems(User user, int pageSize, User lastFollowee, GetFollowObserver observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollowee, new GetFollowingHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);
    }

    public void loadMoreFollowers(User user, int pageSize, User lastFollower, GetFollowObserver observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollower, new GetFollowersHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

    public void updateFollowingAndFollowers(User user, MainPresenter.GetCountObserver observer) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        getFollowersCount(executor, user, observer);
        getFollowingCount(executor, user, observer);
    }

    public void getFollowersCount(ExecutorService executor, User user, MainPresenter.GetCountObserver observer) {
        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetFollowersCountHandler(observer));
        executor.execute(followersCountTask);
    }

    public void getFollowingCount(ExecutorService executor, User user, MainPresenter.GetCountObserver observer) {
        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetFollowingCountHandler(observer));
        executor.execute(followingCountTask);
    }

    public void isFollower(User selectedUser, MainPresenter.IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }

    public void follow(User selectedUser, MainPresenter.FollowObserver observer) {
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);
    }

    public void unfollow(User selectedUser, MainPresenter.FollowObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new UnfollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
    }

}
