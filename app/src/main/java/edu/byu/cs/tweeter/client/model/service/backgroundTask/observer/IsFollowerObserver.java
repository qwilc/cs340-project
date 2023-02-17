package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

public interface IsFollowerObserver extends ServiceObserver {
    void handleSuccess(boolean isFollower);
}
