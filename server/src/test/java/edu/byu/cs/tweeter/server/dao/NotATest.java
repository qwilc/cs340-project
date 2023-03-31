package edu.byu.cs.tweeter.server.dao;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoAuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFeedDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoStoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoUserDAO;
import edu.byu.cs.tweeter.server.dao.factory.AbstractDAOFactory;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;

// TODO I don't know why i didn't just make a main function somewhere :/
public class NotATest {
    @Test
    public void addAllen() {
        AbstractDAOFactory factory = new DynamoDAOFactory();

        FeedDAO feedDAO = new DynamoFeedDAO();
        StoryDAO storyDAO = new DynamoStoryDAO();
        UserDAO userDAO = new DynamoUserDAO();
        AuthtokenDAO authtokenDAO = new DynamoAuthtokenDAO();

        User allen = new User("Allen", "Anderson", "@allen", null);
        Status dummyStatus = new Status("post", allen, 123L, new ArrayList<>(), new ArrayList<>());

        userDAO.addUser(allen.getAlias(), allen.getFirstName(), allen.getLastName(), allen.getImageUrl());
        feedDAO.addFeed("@allen", dummyStatus);
        storyDAO.addStory(dummyStatus);
        authtokenDAO.addAuthtoken(allen.getAlias());
    }
}
