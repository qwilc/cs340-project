package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.AuthenticationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticationServiceObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserver;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;

public class UserService {

    public static final String LOGIN_URL_PATH = "/login";
    public static final String REGISTER_PATH = "/register";

    public void getUser(String userAlias, UserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                userAlias, new GetUserHandler(observer));
        TaskExecutor.executeTask(getUserTask);
    }

    public void login(String alias, String password, AuthenticationServiceObserver observer) {
        LoginTask loginTask = getLoginTask(alias, password, observer);
        TaskExecutor.executeTask(loginTask);
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64, AuthenticationServiceObserver observer) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password,
                imageBytesBase64, new AuthenticationHandler(observer));
        TaskExecutor.executeTask(registerTask);
    }

    public void logout(MainPresenter.LogOutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new SimpleHandler(observer));
        TaskExecutor.executeTask(logoutTask);
    }

    /**
     * Returns an instance of {@link LoginTask}. Allows mocking of the LoginTask class for
     * testing purposes. All usages of LoginTask should get their instance from this method to
     * allow for proper mocking.
     *
     * @return the instance.
     */
    LoginTask getLoginTask(String alias, String password, AuthenticationServiceObserver observer) {
        return new LoginTask(alias, password, new AuthenticationHandler(observer));
    }

}
