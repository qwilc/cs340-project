package edu.byu.cs.tweeter.server.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.dto.FollowBean;
import edu.byu.cs.tweeter.util.Pair;

public class DynamoFollowsDAOTest {

    private DynamoFollowsDAO dao;

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

        dao = new DynamoFollowsDAO();
    }

    @Test
    public void testSuccessfulAdd() {
        dao.addFollow("@testfollower", "Test Follower", "@testfollowee", "Test Followee");
        FollowBean follow = dao.getFollow("@testfollower", "@testfollowee");

        assertNotNull(follow);
        assertEquals("Test Follower", follow.getFollower_name());
        assertEquals("Test Followee", follow.getFollowee_name());

        dao.deleteFollow("@testfollower", "@testfollowee");
    }

    // FIXME: These aren't proper tests. I'm just manually checking what's printed and I'm depending on data that's already there
    @Test
    public void testGetPageFollowees_Success() {
        // Get the first page of followees and print them
        Pair<List<User>, Boolean> followeeDataPage1 = dao.getPageOfFollowees("@FredFlintstone", 10, null);
        List<User> followees1 = followeeDataPage1.getFirst();

        for(User user : followees1) {
            System.out.print(user.getAlias() + ", ");
        }
        System.out.println("");

        // Second page of followees
        String lastFollowee = followees1.get(followees1.size()-1).getAlias();
        Pair<List<User>, Boolean> followeeDataPage2 = dao.getPageOfFollowees("@FredFlintstone", 10, lastFollowee);
        List<User> followees2 = followeeDataPage2.getFirst();

        for(User user : followees2) {
            System.out.print(user.getAlias() + ", ");
        }
        System.out.println("");
    }

    @Test
    public void testGetPageFollowers_Success() {
        // First page of followers
        Pair<List<User>, Boolean> followerDataPage1 = dao.getPageOfFollowers("@ClintEastwood", 10, null);
        List<User> followers1 = followerDataPage1.getFirst();

        for(User user : followers1) {
            System.out.print(user.getAlias() + ", ");
        }
        System.out.println("");

        // Second page of followers
        String lastFollower = followers1.get(followers1.size()-1).getAlias();
        Pair<List<User>, Boolean> followerDataPage2 = dao.getPageOfFollowers("@ClintEastwood", 10, lastFollower);
        List<User> followers2 = followerDataPage2.getFirst();

        for(User user : followers2) {
            System.out.print(user.getAlias() + ", ");
        }
        System.out.println("");
    }
}
