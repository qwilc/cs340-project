package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.User;

public class SecondaryPresenter extends Presenter {

    public SecondaryPresenter(SecondaryView view) {
        super(view);
    }

    public interface SecondaryView extends View {
        void startUserActivity(User user);
    }
}
