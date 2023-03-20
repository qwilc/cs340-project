package edu.byu.cs.tweeter.client.model.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowsRequest;
import edu.byu.cs.tweeter.model.net.request.GetCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.FollowsResponse;
import edu.byu.cs.tweeter.model.net.response.GetCountResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeTest {

    ServerFacade serverFacade;
    FakeData fakeData;

    @BeforeEach
    public void setup() {
        serverFacade = new ServerFacade();
        fakeData = FakeData.getInstance();
    }

    @Test
    public void testRegister_Success() throws IOException, TweeterRemoteException {
        String firstName = "First";
        String lastName = "Last";
        String username = "@username";
        String password = "password";
        String image = "image";

        RegisterRequest request = new RegisterRequest(firstName, lastName, username, password, image);
        AuthenticationResponse response = serverFacade.authenticate(request, UserService.REGISTER_PATH);

        AuthToken expectedToken = fakeData.getAuthToken();
        User expectedUser = fakeData.getFirstUser();

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
        Assertions.assertNotNull(response.getAuthToken());
        // TODO: the fake data should return the same auth token each time, right?
        //Assertions.assertEquals(expectedToken, response.getAuthToken());
        Assertions.assertEquals(expectedUser, response.getUser());
    }

    @Test
    public void testRegister_BadRequest() {
        String firstName = null;
        String lastName = null;
        String username = null;
        String password = null;
        String image = null;

        RegisterRequest request = new RegisterRequest(firstName, lastName, username, password, image);

        Assertions.assertThrows(
                TweeterRequestException.class,
                () -> {
                    serverFacade.authenticate(request, UserService.REGISTER_PATH);
                });
    }

    @Test
    public void testGetFollowers_Success() throws IOException, TweeterRemoteException {
        AuthToken authToken = new AuthToken();
        String alias = "";
        int limit = 3;
        String lastAlias = null;

        FollowsRequest request = new FollowsRequest(authToken, alias, limit, lastAlias);
        FollowsResponse response = serverFacade.getFollowers(request, FollowService.GET_FOLLOWERS_PATH);

        List<User> expectedFollowers = fakeData.getFakeUsers().subList(0, limit);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
        Assertions.assertEquals(expectedFollowers, response.getFollowees());
    }

    @Test
    public void testGetFollowers_BadRequest() {
        AuthToken authToken = new AuthToken();
        String alias = null;
        int limit = 3;
        String lastAlias = null;

        FollowsRequest request = new FollowsRequest(authToken, alias, limit, lastAlias);

        Assertions.assertThrows(
                TweeterRequestException.class,
                () -> {
                    serverFacade.getFollowers(request, FollowService.GET_FOLLOWERS_PATH);
                });
    }

    @Test
    public void testGetFollowingCount_Success() throws IOException, TweeterRemoteException {
        AuthToken authToken = new AuthToken();
        User user = new User("name", "name", "image");

        GetCountRequest request = new GetCountRequest(authToken, user);
        GetCountResponse response = serverFacade.getCount(request, FollowService.GET_FOLLOWING_COUNT_PATH);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
        Assertions.assertEquals(20, response.getCount());
    }

    @Test
    public void testGetFollowingCount_BadRequest() {
        AuthToken authToken = new AuthToken();
        User user = null;

        GetCountRequest request = new GetCountRequest(authToken, user);

        Assertions.assertThrows(
                TweeterRequestException.class,
                () -> {
                    serverFacade.getCount(request, FollowService.GET_FOLLOWING_COUNT_PATH);
                });
    }
}
