package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;

public abstract class AutheticationTask extends BackgroundTask {
    private static final String LOG_TAG = "AuthenticationTask";

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    private String username;
    /**
     * The user's password.
     */
    private String password;

    public AutheticationTask(String username, String password, Handler messageHandler) {
        super(messageHandler);
        this.username = username;
        this.password = password;
    }
}
