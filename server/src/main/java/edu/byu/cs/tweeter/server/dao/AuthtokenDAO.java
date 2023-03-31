package edu.byu.cs.tweeter.server.dao;

public interface AuthtokenDAO {
    public void addAuthtoken(String alias);
    public void removeAuthtoken(String authtoken);
    public String getAlias(String authtoken);

    public long getTimestamp(String authtoken);

    public void updateTimestamp(String authtoken);
}
