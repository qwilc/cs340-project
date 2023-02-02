package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingPresenter {

    private static final int PAGE_SIZE = 10;

    public interface View {

        void displayMessage(String message);

        void addMoreItems(List<User> followees);

        void setLoadingFooter(boolean value);

        void startUserActivity(User user);
    }

    private View view; // no need to have a list because it's one to one

    private FollowService followService;
    private UserService userService;

    private User user;
    private User lastFollowee;

    private boolean hasMorePages;

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    private boolean isLoading = false;

    public GetFollowingPresenter(View view, User user) {
        this.view = view; // one-to-one means don't need to register the usual way
        this.user = user;
        this.followService = new FollowService();
        this.userService = new UserService();
    }

    public void loadMoreItems() {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(true);
            followService.loadMoreItems(user, PAGE_SIZE, lastFollowee, new GetFollowObserver());
        }
    }

    public void getUser(String userAlias) {
        userService.getUser(userAlias, new GetUserObserver());
    }

    public class GetFollowObserver implements FollowService.GetFollowObserver {

        @Override
        public void displayError(String message) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
        }

        @Override
        public void addItems(List<User> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);

            lastFollowee = (items.size() > 0) ? items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems(items);
        }
    }

    public class GetUserObserver implements UserService.GetUserObserver {

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }

        @Override
        public void startUserActivity(User user) {
            view.startUserActivity(user);
        }
    }
}
