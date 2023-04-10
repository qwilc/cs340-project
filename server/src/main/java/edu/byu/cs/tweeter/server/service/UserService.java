package edu.byu.cs.tweeter.server.service;

import static edu.byu.cs.tweeter.server.service.PBKDF2WithHmacSHA1Hashing.generateStrongPasswordHash;
import static edu.byu.cs.tweeter.server.service.PBKDF2WithHmacSHA1Hashing.validatePassword;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.AuthenticationRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.GetCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.dao.factory.AbstractDAOFactory;

public class UserService {

    private AbstractDAOFactory daoFactory;

    public UserService(AbstractDAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public AbstractDAOFactory getDaoFactory() {
        return daoFactory;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        String new_password = request.getPassword();
        String stored_hash = getDaoFactory().getUserDAO().getPasswordHash(request.getUsername());

        try {
            if(getDaoFactory().getUserDAO().isAvailableAlias(request.getUsername())
                || !validatePassword(new_password, stored_hash)) {
                return new AuthenticationResponse("Invalid username or password");
            }
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException("[Server Error] Password validation failed: " + ex.getMessage());
        }
        catch(Exception ex) {
            throw new RuntimeException("[Server Error] Failed to login: " + ex.getMessage());
        }

        User user = getDaoFactory().getUserDAO().getUser(request.getUsername());
        AuthToken authtoken = getDaoFactory().getAuthtokenDAO().createAuthtoken(request.getUsername());

        return new AuthenticationResponse(user, authtoken);
    }

    public AuthenticationResponse register(RegisterRequest request) throws RuntimeException {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        }
        else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        else if(request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a first name");
        }
        else if(request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        }
        else if(request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing an image");
        }

        if(!getDaoFactory().getUserDAO().isAvailableAlias(request.getUsername())) {
            return new AuthenticationResponse("Username is already taken");
        }

        String hashedPassword;
        try {
            System.out.println("before hash");
            hashedPassword = generateStrongPasswordHash(request.getPassword());
            System.out.println("after hash");
        }
        catch(NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException("[Server Error] Failed to process password" + ex.getMessage());
        }

        try {
            String imageURL = getDaoFactory().getImageDAO().storeImage(request.getUsername(), request.getImage());
            getDaoFactory().getUserDAO().addUser(request.getUsername(), hashedPassword,
                    request.getFirstName(), request.getLastName(), imageURL);

            User user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageURL);
            AuthToken authtoken = getDaoFactory().getAuthtokenDAO().createAuthtoken(request.getUsername());
            return new AuthenticationResponse(user, authtoken);
        }
        catch(Exception ex) {
            throw new RuntimeException("[Server Error] Failed to register: " + ex.getMessage());
        }
    }

    public LogoutResponse logout(LogoutRequest request) {
        getDaoFactory().getAuthtokenDAO().removeAuthtoken(request.getAuthToken().getToken());
        return new LogoutResponse();
    }

    public GetUserResponse getUser(GetUserRequest request) {
        boolean isValidAuthtoken = getDaoFactory().getAuthtokenDAO().validateAuthtoken(request.getAuthToken().getToken());
        if (!isValidAuthtoken) {
            return new GetUserResponse("Authtoken has expired");
        }
        return new GetUserResponse(getDaoFactory().getUserDAO().getUser(request.getAlias()));
    }

}
