package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.Request;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    @Override
    protected AuthenticationResponse authenticate(Request request) throws IOException, TweeterRemoteException {
        return getServerFacade().login((LoginRequest) request, UserService.LOGIN_URL_PATH);
    }

    @Override
    protected Request createRequest(String username, String password) {
        return new LoginRequest(username, password);
    }

    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() {
        User loggedInUser = getFakeData().getFirstUser();
        AuthToken authToken = getFakeData().getAuthToken();
        return new Pair<>(loggedInUser, authToken);
    }
}
