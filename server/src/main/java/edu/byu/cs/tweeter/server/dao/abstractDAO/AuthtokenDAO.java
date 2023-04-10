package edu.byu.cs.tweeter.server.dao.abstractDAO;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthtokenDAO {
    boolean isValidAuthtoken(String authtoken);

    public AuthToken createAuthtoken(String alias);
    public void removeAuthtoken(String authtoken);
    public String getAlias(String authtoken);

    public long getTimestamp(String authtoken);

    public void updateTimestamp(String authtoken);

    boolean validateAuthtoken(String token);
}
