package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter {

    public interface View {

        void displayMessage(String message);

        void setRegisterMessage(boolean value);

        void startUserActivity(User registeredUser);

        void validateImage();

        String getEncodedImage();

        void setErrorView(String message);
    }

    private View view;
    private UserService userService;

    public RegisterPresenter(View view) {
        this.view = view;
        this.userService = new UserService();
    }

    public void tryRegister(String firstName, String lastName, String alias, String password) {
        try {
            validateRegistration(firstName, lastName, alias, password);
            view.setErrorView(null);
            view.setRegisterMessage(true);

            String imageBytesBase64 = view.getEncodedImage();

            // Send register request.
            userService.register(firstName, lastName, alias, password, imageBytesBase64, new RegisterObserver());

        } catch (Exception e) {
            view.setErrorView(e.getMessage());
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

        view.validateImage();
    }
    
    public class RegisterObserver implements AuthenticationObserver {

        @Override
        public void handleSuccess(User registeredUser, AuthToken authToken) {

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            view.setRegisterMessage(false);

            view.displayMessage("Hello " + registeredUser.getName());
            try {
                view.startUserActivity(registeredUser);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage(message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage(ex.getMessage());
        }
    }


}
