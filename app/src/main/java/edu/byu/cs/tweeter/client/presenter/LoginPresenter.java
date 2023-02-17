package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticationServiceObserver;

public class LoginPresenter extends AuthenticationPresenter {


    public interface LoginView extends AuthenticationView {

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

    public class LoginObserver extends AuthenticationObserver implements AuthenticationServiceObserver {

        @Override
        public String getPrefix() {
            return "Failed to login";
        }
    }
}
