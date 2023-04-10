package edu.byu.cs.tweeter.server.dao.abstractDAO;

public interface ImageDAO {

    String storeImage(String alias, String image_string);
}
