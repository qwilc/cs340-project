package edu.byu.cs.tweeter.model.net.response;

public class UpdateFollowResponse extends Response {

    public UpdateFollowResponse(String message) {
        super(false, message);
    }

    public UpdateFollowResponse() {
        super(true, null);
    }
}
