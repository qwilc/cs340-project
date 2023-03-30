package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowsRequest;
import edu.byu.cs.tweeter.model.net.response.FollowsResponse;
import edu.byu.cs.tweeter.server.dao.DynamoFollowsDAO;
import edu.byu.cs.tweeter.server.dao.ExampleFollowDAO;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;
import edu.byu.cs.tweeter.util.Pair;

public class FollowServiceTest {

    private FollowsRequest request;
    private Pair<List<User>, Boolean> expectedResponse;
    private DynamoFollowsDAO mockFollowDAO;

    DynamoDAOFactory mockDynamoDAOFactory;
    private FollowService followServiceSpy;

    @BeforeEach
    public void setup() {
        AuthToken authToken = new AuthToken();

        User currentUser = new User("FirstName", "LastName", null);

        User resultUser1 = new User("FirstName1", "LastName1",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        User resultUser2 = new User("FirstName2", "LastName2",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
        User resultUser3 = new User("FirstName3", "LastName3",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");

        // Setup a request object to use in the tests
        request = new FollowsRequest(authToken, currentUser.getAlias(), 3, null);

        // Setup a mock ExampleFollowDAO that will return known responses
        expectedResponse = new Pair(Arrays.asList(resultUser1, resultUser2, resultUser3), false);
        mockFollowDAO = Mockito.mock(DynamoFollowsDAO.class);
        mockDynamoDAOFactory = Mockito.mock(DynamoDAOFactory.class);
        Mockito.when(mockDynamoDAOFactory.getFollowsDAO()).thenReturn(mockFollowDAO);
        Mockito.when(mockFollowDAO.getPageOfFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias()))
                .thenReturn(expectedResponse);

        followServiceSpy = Mockito.spy(FollowService.class);
        Mockito.when(followServiceSpy.getDaoFactory()).thenReturn(mockDynamoDAOFactory);
    }

    /**
     * Verify that the {@link FollowService#getFollowees(FollowsRequest)}
     * method returns the same result as the {@link ExampleFollowDAO} class.
     */
    @Test
    public void testGetFollowees_validRequest_correctResponse() {
        FollowsResponse response = followServiceSpy.getFollowees(request);
        Assertions.assertEquals(expectedResponse.getFirst(), response.getFollowees());
        Assertions.assertEquals(expectedResponse.getSecond(), response.getHasMorePages());
    }
}
