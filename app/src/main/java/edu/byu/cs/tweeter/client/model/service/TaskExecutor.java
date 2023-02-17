package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;

public class TaskExecutor {
    public static void executeTask(BackgroundTask task) { //TODO: Should this be static?
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }
}
