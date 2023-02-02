package edu.byu.cs.tweeter.client.model.service;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {

    public void login(String alias, String password, LoginObserver observer) {
        LoginTask loginTask = new LoginTask(alias, password, new LoginHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(loginTask);
    }

    public interface GetUserObserver {

        void displayMessage(String s);

        void startUserActivity(User user);
    }

    public interface LoginObserver {

        void displayMessage(String s);

        void handleSuccess(User loggedInUser);
    }

    public interface RegisterObserver {}

    public void getUser(String userAlias, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                userAlias, new GetUserHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getUserTask);
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private class GetUserHandler extends Handler {

        private GetUserObserver observer;

        public GetUserHandler(GetUserObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetUserTask.SUCCESS_KEY);
            if (success) {
                User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
                observer.startUserActivity(user);
            } else if (msg.getData().containsKey(GetUserTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetUserTask.MESSAGE_KEY);
                observer.displayMessage("Failed to get user's profile: " + message);
            } else if (msg.getData().containsKey(GetUserTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetUserTask.EXCEPTION_KEY);
                observer.displayMessage("Failed to get user's profile because of exception: " + ex.getMessage());
            }
        }
    }

    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private class LoginHandler extends Handler {

        private LoginObserver observer;

        public LoginHandler(LoginObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(LoginTask.SUCCESS_KEY);
            if (success) {
                User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

                // Cache user session information
                Cache.getInstance().setCurrUser(loggedInUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);

                observer.handleSuccess(loggedInUser);

            } else if (msg.getData().containsKey(LoginTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LoginTask.MESSAGE_KEY);
                observer.displayMessage("Failed to login: " + message);

            } else if (msg.getData().containsKey(LoginTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LoginTask.EXCEPTION_KEY);
                observer.displayMessage("Failed to login because of exception: " + ex.getMessage());
            }
        }
    }

    // RegisterHandler

    private class RegisterHandler extends Handler {

        public RegisterHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(RegisterTask.SUCCESS_KEY);
            if (success) {
                User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

                Intent intent = new Intent(getContext(), MainActivity.class);

                Cache.getInstance().setCurrUser(registeredUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);

                intent.putExtra(MainActivity.CURRENT_USER_KEY, registeredUser);

                registeringToast.cancel();

                Toast.makeText(getContext(), "Hello " + Cache.getInstance().getCurrUser().getName(), Toast.LENGTH_LONG).show();
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else if (msg.getData().containsKey(RegisterTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(RegisterTask.MESSAGE_KEY);
                Toast.makeText(getContext(), "Failed to register: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(RegisterTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(RegisterTask.EXCEPTION_KEY);
                Toast.makeText(getContext(), "Failed to register because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
