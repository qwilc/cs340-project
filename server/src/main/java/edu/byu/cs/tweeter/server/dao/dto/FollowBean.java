package edu.byu.cs.tweeter.server.dao.dto;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DynamoFollowsDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class FollowBean {
    private String follower_handle;
    private String followee_handle;
    private String follower_name;
    private String followee_name;
    private String follower_image_url; // TODO: url, right?
    private String followee_image_url;

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

    public String getFollower_name() {
        return follower_name;
    }

    public void setFollower_name(String follower_name) {
        this.follower_name = follower_name;
    }

    public String getFollowee_name() {
        return followee_name;
    }

    public void setFollowee_name(String followee_name) {
        this.followee_name = followee_name;
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

    public User getFollowerAsUser() {
        String[] name = follower_name.split(" ");
        return new User(name[0], name[1], follower_handle, follower_image_url);
    }

    public User getFolloweeAsUser() {
        String[] name = followee_name.split(" ");
        return new User(name[0], name[1], followee_handle, followee_image_url);
    }
}
