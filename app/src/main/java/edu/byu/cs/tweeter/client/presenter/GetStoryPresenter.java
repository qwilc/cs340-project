package edu.byu.cs.tweeter.client.presenter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetStoryPresenter {
    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private class GetStoryHandler extends Handler {

        public GetStoryHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            isLoading = false;
            removeLoadingFooter();

            boolean success = msg.getData().getBoolean(GetStoryTask.SUCCESS_KEY);
            if (success) {
                List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.STATUSES_KEY);
                hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);

                lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;

                storyRecyclerViewAdapter.addItems(statuses);
            } else if (msg.getData().containsKey(GetStoryTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetStoryTask.MESSAGE_KEY);
                Toast.makeText(getContext(), "Failed to get story: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(GetStoryTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetStoryTask.EXCEPTION_KEY);
                Toast.makeText(getContext(), "Failed to get story because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
