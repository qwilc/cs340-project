package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends Presenter {

    public interface RegisterView extends AuthenticationPresenter.AuthenticationView {

        void setRegisterMessage(boolean value);

        void startUserActivity(User registeredUser);

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
            ((RegisterView)getView()).setRegisterMessage(true);

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

        ((RegisterView)getView()).validateImage();
    }
    
    public class RegisterObserver extends Observer implements AuthenticationObserver {

        @Override
        public void handleSuccess(User registeredUser, AuthToken authToken) {

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            ((RegisterView)getView()).setRegisterMessage(false);

            ((RegisterView)getView()).displayMessage("Hello " + registeredUser.getName());
            try {
                ((RegisterView)getView()).startUserActivity(registeredUser);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


}
