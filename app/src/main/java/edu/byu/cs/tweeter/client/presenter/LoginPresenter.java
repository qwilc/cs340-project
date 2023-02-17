package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticationServiceObserver;

public class LoginPresenter extends AuthenticationPresenter {

    public LoginPresenter(AuthenticationView view) {
        super(view);
    }

    public void tryLogin(String alias, String password) { // TODO: Combine with tryRegister somehow? Or too different?
        try {
            validateLoginInfo(alias, password);
            ((AuthenticationView)getView()).setErrorView(null);
            getUserService().login(alias, password, new LoginObserver());

        } catch (Exception e) {
            ((AuthenticationView)getView()).setErrorView(e.getMessage());
        }
    }

    public class LoginObserver extends AuthenticationObserver implements AuthenticationServiceObserver {
        @Override
        public String getPrefix() {
            return "Failed to login";
        }
    }
}
