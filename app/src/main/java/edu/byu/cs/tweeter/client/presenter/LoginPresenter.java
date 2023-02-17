package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends AuthenticationPresenter {


    public interface LoginView extends AuthenticationView {

        void setLoginMessage(boolean value);

    }

    public LoginPresenter(LoginView view) {
        super(view);
    }

    public void tryLogin(String alias, String password) {
        try {
            validateLogin(alias, password);
            ((LoginView)getView()).setErrorView(null);
            getUserService().login(alias, password, new LoginObserver());

        } catch (Exception e) {
            ((LoginView)getView()).setErrorView(e.getMessage());
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

    public class LoginObserver extends Observer implements AuthenticationObserver {

        @Override
        public void handleSuccess(User loggedInUser, AuthToken authToken) {
            // Cache user session information
            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            ((LoginView)getView()).setLoginMessage(false);
            ((LoginView)getView()).displayMessage("Hello " + loggedInUser.getName());

            ((LoginView)getView()).startUserActivity(loggedInUser);
        }
    }
}
