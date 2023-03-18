package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.User;

public class FragmentPresenter extends Presenter {

    public FragmentPresenter(FragmentView view) {
        super(view);
    }

    public interface FragmentView extends View {
        void startUserActivity(User user);
    }
}
