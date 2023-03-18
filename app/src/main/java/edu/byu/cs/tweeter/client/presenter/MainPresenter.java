package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.GetCountObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter {

    public interface MainView extends View {

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

    private FollowService followService;
    private StatusService statusService;

    private User selectedUser;

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }
    
    public MainPresenter(MainView view) {
        super(view);
        followService = new FollowService();
    }

    protected StatusService getStatusService() {
        if(statusService == null) {
            statusService = new StatusService();
        }

        return statusService;
    }

    public void updateFollowingAndFollowers() {
        followService.updateFollowingAndFollowers(selectedUser, new GetFollowerCountObserver(), new GetFolloweeCountObserver());
    }

    public void displayFollowButton() {
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            ((MainView)getView()).setFollowButtonVisibility(false);
        } else {
            ((MainView)getView()).setFollowButtonVisibility(true);
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
        getUserService().logout(new LogOutObserver());
    }

    public void clearCache() {
        Cache.getInstance().clearCache();
    }

    public void postStatus(String post, String tag) {
        ((MainView)getView()).setPostingMessage(true);
        Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), System.currentTimeMillis(), parseURLs(post), parseMentions(post));
        getStatusService().postStatus(newStatus, new PostStatusObserver());
    }

    public class IsFollowerObserver extends Observer implements edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.IsFollowerObserver {
        @Override
        public void handleSuccess (boolean isFollower) {
            ((MainView)getView()).displayFollowButton(isFollower);
        }

        @Override
        public String getPrefix() {
            return "Failed to check if user is follower";
        }
    }

    public class GetFollowerCountObserver extends Observer implements GetCountObserver {
        @Override
        public void handleSuccess(int count) {
            ((MainView)getView()).displayFollowerCount(String.valueOf(count));
        }

        @Override
        public String getPrefix() {
            return "Failed to get follower count";
        }
    }

    public class GetFolloweeCountObserver extends Observer implements GetCountObserver {
        @Override
        public void handleSuccess(int count) {
            ((MainView)getView()).displayFolloweeCount(String.valueOf(count));
        }

        @Override
        public String getPrefix() {
            return "Failed to get followee count";
        }
    }

    public abstract class FollowButtonObserver extends Observer implements SimpleNotificationObserver {
        @Override
        public void handleSuccess() {
            followService.updateFollowingAndFollowers(selectedUser, new GetFollowerCountObserver(), new GetFolloweeCountObserver());
            ((MainView)getView()).setFollowButtonEnabled(true);
        }

        @Override
        public void handleFailure(String message) {
            super.handleFailure(message);
            ((MainView)getView()).setFollowButtonEnabled(true);
        }

        @Override
        public void handleException(Exception ex) {
            super.handleException(ex);
            ((MainView)getView()).setFollowButtonEnabled(true);
        }
    }

    public class FollowObserver extends FollowButtonObserver implements SimpleNotificationObserver {
        @Override
        public void handleSuccess() {
            super.handleSuccess();
            ((MainView)getView()).updateFollowButton(false);
        }

        @Override
        public String getPrefix() {
            return "Failed to follow";
        }
    }

    public class UnfollowObserver extends FollowButtonObserver implements SimpleNotificationObserver {
        @Override
        public void handleSuccess() {
            super.handleSuccess();
            ((MainView)getView()).updateFollowButton(true);
        }

        @Override
        public String getPrefix() {
            return "Failed to unfollow";
        }
    }

    public class LogOutObserver extends Observer implements SimpleNotificationObserver {

        @Override
        public void handleSuccess() {
            ((MainView)getView()).setLogOutMessage(false);
            ((MainView)getView()).logoutUser();
        }

        @Override
        public String getPrefix() {
            return "Failed to logout";
        }
    }

    public class PostStatusObserver extends Observer implements SimpleNotificationObserver {

        @Override
        public void handleSuccess() {
            ((MainView)getView()).setPostingMessage(false);
            getView().displayMessage("Successfully Posted!");
        }

        @Override
        public void handleFailure(String message) {
            ((MainView)getView()).setPostingMessage(false);
            super.handleFailure(message);
        }

        @Override
        public void handleException(Exception ex) {
            ((MainView)getView()).setPostingMessage(false);
            super.handleException(ex);
        }

        @Override
        public String getPrefix() {
            return "Failed to post status";
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
}
