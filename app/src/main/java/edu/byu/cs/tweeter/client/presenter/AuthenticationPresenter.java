package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticationPresenter extends FragmentPresenter {
    public AuthenticationPresenter(AuthenticationView view) {
        super(view);
    }

    public interface AuthenticationView extends FragmentView {
        void setErrorView(String message);
        void setAuthenticationMessage(boolean value);
    }

    public void validateLoginInfo(String alias, String password) {
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    public abstract class AuthenticationObserver extends Observer {

        public void handleSuccess(User user, AuthToken authToken) {

            Cache.getInstance().setCurrUser(user);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            ((AuthenticationView)getView()).setAuthenticationMessage(false);

            ((AuthenticationView)getView()).displayMessage("Hello " + user.getName());
            try {
                ((AuthenticationView)getView()).startUserActivity(user);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
