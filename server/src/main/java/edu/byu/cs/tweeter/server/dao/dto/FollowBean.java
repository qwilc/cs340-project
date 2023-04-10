package edu.byu.cs.tweeter.server.dao.dto;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFollowsDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class FollowBean {
    private String follower_handle;
    private String followee_handle;
    private String follower_firstname;
    private String follower_lastname;
    private String followee_firstname;
    private String followee_lastname;
    private String follower_image_url;
    private String followee_image_url;

    public FollowBean() {
    }

    public FollowBean(String follower_handle, String followee_handle, String follower_firstname, String follower_lastname, String followee_firstname, String followee_lastname, String follower_image_url, String followee_image_url) {
        this.follower_handle = follower_handle;
        this.followee_handle = followee_handle;
        this.follower_firstname = follower_firstname;
        this.follower_lastname = follower_lastname;
        this.followee_firstname = followee_firstname;
        this.followee_lastname = followee_lastname;
        this.follower_image_url = follower_image_url;
        this.followee_image_url = followee_image_url;
    }

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = DynamoFollowsDAO.IndexName)
    public String getFollower_handle() {
        return follower_handle;
    }

    public void setFollower_handle(String follower_handle) {
        this.follower_handle = follower_handle;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = DynamoFollowsDAO.IndexName)
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollowee_handle(String followee_handle) {
        this.followee_handle = followee_handle;
    }

    public String getFollower_firstname() {
        return follower_firstname;
    }

    public void setFollower_firstname(String follower_firstname) {
        this.follower_firstname = follower_firstname;
    }

    public String getFollowee_firstname() {
        return followee_firstname;
    }

    public void setFollowee_firstname(String followee_firstname) {
        this.followee_firstname = followee_firstname;
    }

    public String getFollower_lastname() {
        return follower_lastname;
    }

    public void setFollower_lastname(String follower_lastname) {
        this.follower_lastname = follower_lastname;
    }

    public String getFollowee_lastname() {
        return followee_lastname;
    }

    public void setFollowee_lastname(String followee_lastname) {
        this.followee_lastname = followee_lastname;
    }

    public String getFollower_image_url() {
        return follower_image_url;
    }

    public void setFollower_image_url(String follower_image_url) {
        this.follower_image_url = follower_image_url;
    }

    public String getFollowee_image_url() {
        return followee_image_url;
    }

    public void setFollowee_image_url(String followee_image_url) {
        this.followee_image_url = followee_image_url;
    }

    // FIXME: parsing names for these is extremely brittle rn
    public User getFollowerAsUser() {
        return new User(follower_firstname, follower_lastname, follower_handle, follower_image_url);
    }

    public User getFolloweeAsUser() {
        return new User(followee_firstname, followee_lastname, followee_handle, followee_image_url);
    }
}
