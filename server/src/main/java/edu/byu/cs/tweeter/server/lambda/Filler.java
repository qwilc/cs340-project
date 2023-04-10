package edu.byu.cs.tweeter.server.lambda;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.server.dao.abstractDAO.UserDAO;
import edu.byu.cs.tweeter.server.dao.abstractDAO.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.dto.FollowBean;
import edu.byu.cs.tweeter.server.dao.dto.UserBean;
import edu.byu.cs.tweeter.server.dao.factory.FactoryManager;

public class Filler {

    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 30;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@mr_big";
    private final static String TARGET_FIRST_NAME = "Mr";
    private final static String TARGET_LAST_NAME = "Big";
    private final static String IMG_URL = "https://cdn.pixabay.com/photo/2017/09/07/09/55/square-2724387_960_720.jpg";

    public static void fill(String[] args) {

        // Get instance of DAOs by way of the Abstract Factory Pattern
        UserDAO userDAO = FactoryManager.getDAOFactory().getUserDAO();
        FollowsDAO followDAO = FactoryManager.getDAOFactory().getFollowsDAO();

        List<FollowBean> follows = new ArrayList<>();
        List<UserBean> users = new ArrayList<>();

//        FactoryManager.getDAOFactory().getUserDAO().addUser(FOLLOW_TARGET, "x", "targetFirst", "targetLast", IMG_URL);

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String name = "Gal " + i;
            String alias = "@gal" + i;

            UserBean user = new UserBean();
            user.setAlias(alias);
            user.setFirstName(name);
            user.setLastName(name);
            user.setFollowerCount(0);
            user.setFolloweeCount(0);
            user.setImage_url(IMG_URL);
            users.add(user);

            FollowBean follow = new FollowBean(alias, FOLLOW_TARGET, name, name, TARGET_FIRST_NAME, TARGET_LAST_NAME, IMG_URL, IMG_URL);
            follows.add(follow);
        }

        // Call the DAOs for the database logic
        if (users.size() > 0) {
            userDAO.addUserBatch(users);
        }
        if (follows.size() > 0) {
            followDAO.addFollowersBatch(follows);
        }
    }
}
