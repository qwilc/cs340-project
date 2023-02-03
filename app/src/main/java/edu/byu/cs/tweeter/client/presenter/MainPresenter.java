package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter {

    public void unfollow() {
        followService.unfollow(selectedUser, new FollowObserver());

    }

    public void follow() {
        followService.follow(selectedUser, new FollowObserver());

    }

    public void logout() {
        userService.logout(new LogOutObserver());
    }

    public void clearCache() {
        Cache.getInstance().clearCache();
    }

    public void tryStatusPost(String post, String tag) {
        try {
            statusService.postStatus(post, new PostStatusObserver());
        } catch (Exception ex) {
            Log.e(tag, ex.getMessage(), ex);
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    public interface View {

        void displayMessage(String message);

        void displayFollowerCount(String count);

        void displayFolloweeCount(String count);

        void displayFollowButton(boolean isFollower);

        void updateFollowButton(boolean b);

        void setFollowButtonEnabled(boolean b);

        void setFollowButtonVisibility(boolean value);

        void logoutUser();

        void setLogOutMessage(boolean b);

        void setPostingMessage(boolean value);
    }
    
    private View view;
    private FollowService followService;
    private StatusService statusService;
    private UserService userService;

    private User selectedUser;

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }
    
    public MainPresenter(View view) {
        this.view = view;
        followService = new FollowService();
        statusService = new StatusService();
        userService = new UserService();
    }

    public void updateFollowingAndFollowers() {
        followService.updateFollowingAndFollowers(selectedUser, new GetCountObserver());
    }

    public void displayFollowButton() {
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            view.setFollowButtonVisibility(false);
        } else {
            view.setFollowButtonVisibility(true);
            followService.isFollower(selectedUser, new IsFollowerObserver());
        }
    }

    public class IsFollowerObserver implements FollowService.IsFollowerObserver {
        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayFollowButton(boolean isFollower) {
            view.displayFollowButton(isFollower);
        }
    }

    public class GetCountObserver implements FollowService.GetCountObserver {
        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayFollowerCount(int count) {
            view.displayFollowerCount(String.valueOf(count));
        }

        @Override
        public void displayFolloweeCount(int count) {
            view.displayFolloweeCount(String.valueOf(count));
        }

    }

    public class FollowObserver implements FollowService.FollowObserver, FollowService.UnfollowObserver {
        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }

        @Override
        public void setFollowButtonEnabled(boolean b) {
            view.setFollowButtonEnabled(b);
        }

        @Override
        public void updateFollowingAndFollowers() {
            followService.updateFollowingAndFollowers(selectedUser, new GetCountObserver());
        }

        @Override
        public void updateFollowButton(boolean b) {
            view.updateFollowButton(b);
        }
    }

    public class LogOutObserver implements UserService.LogoutObserver {
        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }

        @Override
        public void handleSuccess() {
            view.setLogOutMessage(false);
            view.logoutUser();
        }
    }

    public class PostStatusObserver implements StatusService.PostStatusObserver {

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }

        @Override
        public void setPostingMessage(boolean value) {
            view.setPostingMessage(value);
        }
    }
}
