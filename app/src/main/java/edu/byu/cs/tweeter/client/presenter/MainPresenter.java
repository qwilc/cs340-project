package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter {

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

    public void unfollow() {
        followService.unfollow(selectedUser, new UnfollowObserver());

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
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), System.currentTimeMillis(), parseURLs(post), parseMentions(post));
            statusService.postStatus(newStatus, new PostStatusObserver());
        } catch (Exception ex) {
            Log.e(tag, ex.getMessage(), ex);
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
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
        public void handleSuccess() {
            followService.updateFollowingAndFollowers(selectedUser, new GetCountObserver());
            view.updateFollowButton(false);
            view.setFollowButtonEnabled(true);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage(message);
            view.setFollowButtonEnabled(true);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to follow because of exception: " + ex.getMessage());
            view.setFollowButtonEnabled(true);
        }
    }

    public class UnfollowObserver implements FollowService.UnfollowObserver {
        @Override
        public void handleSuccess() {
            followService.updateFollowingAndFollowers(selectedUser, new GetCountObserver());
            view.updateFollowButton(true);
            view.setFollowButtonEnabled(true);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage(message);
            view.setFollowButtonEnabled(true);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to unfollow because of exception: " + ex.getMessage());
            view.setFollowButtonEnabled(true);
        }
    }

    public class LogOutObserver implements UserService.LogoutObserver {

        @Override
        public void handleSuccess() {
            view.setLogOutMessage(false);
            view.logoutUser();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage(message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to log out because of exception: " + ex.getMessage());
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
