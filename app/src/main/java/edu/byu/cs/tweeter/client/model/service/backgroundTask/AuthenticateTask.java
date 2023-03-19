package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.Request;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.util.Pair;

public abstract class AuthenticateTask extends BackgroundTask {

    private static final String LOG_TAG = "AuthenticationTask";

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    private User authenticatedUser;

    private AuthToken authToken;

    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    protected final String username;

    /**
     * The user's password.
     */
    protected final String password;

    protected AuthenticateTask(Handler messageHandler, String username, String password) {
        super(messageHandler);
        this.username = username;
        this.password = password;
    }


    @Override
    protected final void runTask()  throws IOException {
        try {
            Request request = createRequest(username, password);
            AuthenticationResponse response = authenticate(request);

            if (response.isSuccess()) {
                this.authenticatedUser = response.getUser();
                this.authToken = response.getAuthToken();
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
//        Pair<User, AuthToken> loginResult = runAuthenticationTask();
//
//        authenticatedUser = loginResult.getFirst();
//        authToken = loginResult.getSecond();
//
//        // Call sendSuccessMessage if successful
//        sendSuccessMessage();
//        // or call sendFailedMessage if not successful
//        // sendFailedMessage()
    }

    protected abstract AuthenticationResponse authenticate(Request request) throws IOException, TweeterRemoteException;

    protected abstract Request createRequest(String username, String password);

    protected abstract Pair<User, AuthToken> runAuthenticationTask();

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, authenticatedUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
    }
}
