package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.ServiceObserver;

public abstract class Presenter {
    public interface View {
        void displayMessage(String message);
    }


    private View view;
    private UserService userService;

    protected View getView() {
        return this.view;
    }

    public UserService getUserService() {
        return userService;
    }

    public Presenter(View view) {
        this.view = view;
        this.userService = new UserService();
    }

    public abstract class Observer implements ServiceObserver {

        @Override
        public void handleFailure(String message) {
            view.displayMessage(getPrefix() + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage(getPrefix() + "due to exception: " + ex.getMessage());
        }

        public abstract String getPrefix();
    }
}
