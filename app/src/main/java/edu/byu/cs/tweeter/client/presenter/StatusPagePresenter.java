package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class StatusPagePresenter extends PagedPresenter<Status> {
    private StatusService statusService;

    public StatusService getStatusService() {
        return statusService;
    }

    public StatusPagePresenter(PagedView<Status> view, User user) {
        super(view, user);
        this.statusService = new StatusService();
    }
}
