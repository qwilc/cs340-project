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

    public void login(String alias, String password) {
        userService.login(alias, password, new LoginObserver());

    }

    public void tryLogin(String alias, String password) {
        try {
            validateLogin(alias, password);
            view.setErrorView(null);
            login(alias, password);

        } catch (Exception e) {
            view.setErrorView(e.getMessage());
        }
    }


    public void validateLogin(String alias, String password) { // TODO: Might need to double check that length and charAt are working
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
        public void displayMessage(String s) {
            view.displayMessage(s);
        }

        @Override
        public void handleSuccess(User loggedInUser) {
            view.setLoginMessage(false);
            view.displayMessage("Hello " + loggedInUser.getName());

            view.startUserActivity(loggedInUser);
        }
    }
}
