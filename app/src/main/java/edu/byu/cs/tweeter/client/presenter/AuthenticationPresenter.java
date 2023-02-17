package edu.byu.cs.tweeter.client.presenter;

public class AuthenticationPresenter extends FragmentPresenter {
    public AuthenticationPresenter(AuthenticationView view) {
        super(view);
    }

    public interface AuthenticationView extends SecondaryView {
        void setErrorView(String message);
    }
}
