package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

public interface IsFollowerObserverInterface extends ServiceObserver { //TODO: Prolly rename
    public void handleSuccess(boolean isFollower);
}
