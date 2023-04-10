package edu.byu.cs.tweeter.server.dao.factory;

public class FactoryManager {
    public static AbstractDAOFactory getDAOFactory() {
        return new DynamoDAOFactory();
    }
}
