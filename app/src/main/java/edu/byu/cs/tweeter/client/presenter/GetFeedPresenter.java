package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFeedPresenter {

    private static final int PAGE_SIZE = 10;

    public interface View {

        void displayMessage(String message);

        void setLoadingFooter(boolean value);

        void addMoreItems(List<Status> statuses);

        void startUserActivity(User user);
    }

    private View view;

    private UserService userService;
    private StatusService statusService;

    private User user;
    private Status lastStatus;

    private boolean hasMorePages;
    private boolean isLoading = false;

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

    public GetFeedPresenter(View view, User user) {
        this.view = view;
        this.user = user;
        this.statusService = new StatusService();
        this.userService = new UserService();
    }

    public void getUser(String alias) {
        userService.getUser(alias, new GetUserObserver());
    }

    public void loadMoreItems() {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(true);

            statusService.loadMoreItems(user, PAGE_SIZE, lastStatus, new GetFeedObserver());
        }
    }

    public class GetFeedObserver implements PagedObserver<Status> {

        @Override
        public void handleSuccess(List<Status> statuses, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);

            lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems(statuses);
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(message);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(ex.getMessage());
        }
    }

    public class GetUserObserver implements UserService.GetUserObserver {

        @Override
        public void handleFailure(String message) {
            view.displayMessage(message);
        }

        @Override
        public void handleSuccess(User user) {
            view.startUserActivity(user);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage(ex.getMessage());
        }
    }
}
