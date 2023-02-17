package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter {


    public interface View {

        void displayMessage(String s);

        void setLoginMessage(boolean value);

        void setErrorView(String message);

        void startUserActivity(User user);
    }

    private View view;
    private UserService userService;

    public LoginPresenter(View view) {
        this.view = view;
        userService = new UserService();
    }

    public void tryLogin(String alias, String password) {
        try {
            validateLogin(alias, password); // TODO Does validation need to be pushed to service?
            view.setErrorView(null);
            userService.login(alias, password, new LoginObserver());

        } catch (Exception e) {
            view.setErrorView(e.getMessage());
        }
    }


    public void validateLogin(String alias, String password) {
        if (alias.length() > 0 && alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    public class LoginObserver implements UserService.LoginObserver {

        @Override
        public void handleSuccess(User loggedInUser) {
            view.setLoginMessage(false);
            view.displayMessage("Hello " + loggedInUser.getName());

            view.startUserActivity(loggedInUser);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to login: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to logout because of exception: " + ex.getMessage());
        }
    }
}
