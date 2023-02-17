package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserver;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends FragmentPresenter {

    protected static final int PAGE_SIZE = 10; //TODO: Is this fine here? And fine static final?

    public interface PagedView<T> extends SecondaryView {
        void addMoreItems(List<T> items);

        void setLoadingFooter(boolean value);
    }

    public PagedPresenter(PagedView<T> view, User user) {
        super(view);
    }

    private User user;
    private T lastItem;

    private boolean hasMorePages;
    private boolean isLoading = false;

    public User getUser() {
        return user;
    }

    public T getLastItem() {
        return lastItem;
    }

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

    public void loadMoreItems() {
        if (!isLoading()) {
            setLoading(true);
            ((PagedView)getView()).setLoadingFooter(true);
            callService();
        }
    }

    public abstract void callService();

    public class PagedObserver extends Observer implements edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver<T> {

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {
            isLoading = false;
            ((PagedView<T>)getView()).setLoadingFooter(false);

            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            ((PagedView<T>)getView()).addMoreItems(items);
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            ((PagedView<T>)getView()).setLoadingFooter(false);
            super.handleFailure(message);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            ((PagedView<T>)getView()).setLoadingFooter(false);
            super.handleException(ex);
        }
    }

    public class GetUserObserver extends Observer implements UserObserver {
        @Override
        public void handleSuccess(User user) {
            ((PagedView<T>)getView()).startUserActivity(user);
        } //TODO: This could be a template method in FragmentPresenter
    }
}
