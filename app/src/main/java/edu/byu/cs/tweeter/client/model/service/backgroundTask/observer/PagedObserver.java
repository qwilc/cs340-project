package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

import java.util.List;

public interface PagedObserver<T> extends ServiceObserver {
    public void handleSuccess(List<T> items, boolean hasMorePages);
}
