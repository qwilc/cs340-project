package edu.byu.cs.tweeter.server.dao;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FeedDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.StoryDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFeedDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoStoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoUserDAO;

// TODO I don't know why i didn't just make a main function somewhere :/
public class NotATest {
//    @Test
//    public void addAllen() {
//        AbstractDAOFactory factory = new DynamoDAOFactory();
//
//        FeedDAO feedDAO = new DynamoFeedDAO();
//        StoryDAO storyDAO = new DynamoStoryDAO();
//        UserDAO userDAO = new DynamoUserDAO();
//        AuthtokenDAO authtokenDAO = new DynamoAuthtokenDAO();
//
//        User allen = new User("Allen", "Anderson", "@allen", null);
//        Status dummyStatus = new Status("post", allen, 123L, new ArrayList<>(), new ArrayList<>());
//
//        userDAO.addUser(allen.getAlias(), imageURL, allen.getFirstName(), allen.getLastName(), allen.getImageUrl());
//        feedDAO.addFeed("@allen", dummyStatus);
//        storyDAO.addStatus(dummyStatus);
//        authtokenDAO.createAuthtoken(allen.getAlias());
//    }

    @Test
    public void addStatus() {
        FeedDAO feedDAO = new DynamoFeedDAO();
        StoryDAO storyDAO = new DynamoStoryDAO();
        UserDAO userDAO = new DynamoUserDAO();

        String alias1 = "@b";
        String alias2 = "@c";
        String firstname = "B1";
        String lastname = "B2";
        String content = "This is a test post @c";
        long time = 1680283920141L;
        List<String> urls = new ArrayList<String>();
        ArrayList<String> mentions = new ArrayList<String>();
        mentions.add("@c");
        String img_url = "https://images.pexels.com/photos/6957569/pexels-photo-6957569.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1";

        User user = new User(firstname, lastname, alias1, img_url);
        Status status = new Status(content, user, time, urls, mentions);
        feedDAO.addFeed(alias2, status);
        storyDAO.addStatus(status);

    }
}
