package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticationServiceObserver;

public class RegisterPresenter extends AuthenticationPresenter {

    public interface RegisterView extends AuthenticationPresenter.AuthenticationView {

        void validateImage();

        String getEncodedImage();

    }

    public RegisterPresenter(RegisterView view) {
        super(view);
    }

    public void tryRegister(String firstName, String lastName, String alias, String password) {
        try {
            validateRegistration(firstName, lastName, alias, password);
            ((RegisterView)getView()).setErrorView(null);
            ((RegisterView)getView()).setAuthenticationMessage(true);

            String imageBytesBase64 = ((RegisterView)getView()).getEncodedImage();

            // Send register request.
            getUserService().register(firstName, lastName, alias, password, imageBytesBase64, new RegisterObserver());

        } catch (Exception e) {
            ((RegisterView)getView()).setErrorView(e.getMessage());
        }
    }

    public void validateRegistration(String firstName, String lastName, String alias, String password) {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        validateLoginInfo(alias, password);

        ((RegisterView)getView()).validateImage();
    }
    
    public class RegisterObserver extends AuthenticationObserver implements AuthenticationServiceObserver {
        @Override
        public String getPrefix() {
            return "Failed to register";
        }
    }
}
