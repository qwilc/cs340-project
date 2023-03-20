package edu.byu.cs.tweeter.model.net.response;

public class GetCountResponse extends Response {
    private int count;

    public GetCountResponse(String message) {
        super(false, message);
    }

    public GetCountResponse(int count) {
        super(true, null);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
