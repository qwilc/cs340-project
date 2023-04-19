package edu.byu.cs.tweeter.client.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.AuthenticationRequest;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;

public class PostStatusTest {
    private CountDownLatch countDownLatch;

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @Test
    public void testPostStatus_Success() throws InterruptedException, IOException, TweeterRemoteException {
        resetCountDownLatch();
        ServerFacade serverFacade = new ServerFacade();

        // Login user and store data in Cache
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("@q", "q");
        AuthenticationResponse authenticationResponse = serverFacade.authenticate(authenticationRequest, UserService.LOGIN_PATH);
        User user = authenticationResponse.getUser();
        AuthToken authToken = authenticationResponse.getAuthToken();
        Cache.getInstance().setCurrUser(user);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        MainPresenter.MainView mockView = Mockito.mock(MainPresenter.MainView.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                countDownLatch.countDown();
                return null;
            }
        }).when(mockView).displayMessage("Successfully Posted!");

        MainPresenter spyPresenter = Mockito.spy(new MainPresenter(mockView));

        String post = "Hello @q";
        Long currentTime = System.currentTimeMillis();
        List<String> urls = spyPresenter.parseURLs(post);
        List<String> mentions = spyPresenter.parseMentions(post);
        Status expectedStatus = new Status(post, user, currentTime, urls, mentions);

        Mockito.doReturn(currentTime).when(spyPresenter).getTime();

        spyPresenter.postStatus(post, null);
        awaitCountDownLatch();
        Mockito.verify(mockView).displayMessage("Successfully Posted!");

        StatusRequest request = new StatusRequest(Cache.getInstance().getCurrUserAuthToken(), user.getAlias(), 50, null);
        StatusResponse response = serverFacade.getStory(request, StatusService.GET_STORY_PATH);

        assertTrue(response.isSuccess());

        List<Status> statuses = response.getStatuses();
        Status testStatus = statuses.get(0);

        assertNotNull(testStatus);
        assertEquals(expectedStatus, testStatus);

        User testUser = testStatus.getUser();
        assertNotNull(testUser);
        assertEquals(user, testUser);
    }
}
