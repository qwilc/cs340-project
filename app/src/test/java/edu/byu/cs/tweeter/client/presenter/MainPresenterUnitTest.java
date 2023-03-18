package edu.byu.cs.tweeter.client.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;

public class MainPresenterUnitTest {

    String status_content = "Test";
    String errorMessage = "error message";
    private MainPresenter.MainView mockView;
    private StatusService mockStatusService;
    private MainPresenter mainPresenterSpy;

    @BeforeEach
    public void setup() {
        mockView = Mockito.mock(MainPresenter.MainView.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));

        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);
    }

    @Test
    public void testPostStatus_postStatusSuccessful() {
        Answer<Void> answer = new ObserverSuccessAnswer();
        setUpAndCallPostStatus(answer);
        verifyResults("Successfully Posted!");
    }

    @Test
    public void testPostStatus_postStatusFailed() {
        Answer<Void> answer = new ObserverFailureAnswer();
        setUpAndCallPostStatus(answer);
        verifyResults("Failed to post status: " + errorMessage);
    }

    @Test
    public void testPostStatus_postStatusFailedWithException() {
        Answer<Void> answer = new ObserverExceptionAnswer();
        setUpAndCallPostStatus(answer);
        verifyResults("Failed to post status due to exception: " + errorMessage);
    }

    private void setUpAndCallPostStatus(Answer<Void> answer) {
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus(status_content, "MainActivity");
    }

    private void verifyResults(String message) {
        Mockito.verify(mockView).setPostingMessage(true);
        Mockito.verify(mockView).displayMessage(message);
        Mockito.verify(mockView).setPostingMessage(false);
    }

    private abstract class ObserverAnswer implements Answer<Void> {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            verifyParameters(invocation);
            MainPresenter.PostStatusObserver observer = invocation.getArgument(1, MainPresenter.PostStatusObserver.class);
            handleCase(observer);
            return null;
        }

        protected abstract void handleCase(MainPresenter.PostStatusObserver observer);

        private void verifyParameters(InvocationOnMock invocation) {
            //Check that the parameters are not null
            assertNotNull(invocation.getArgument(0));
            assertNotNull(invocation.getArgument(1));

            // Check that the parameters are the right classes
            assertEquals(Status.class, invocation.getArgument(0).getClass());
            assertEquals(MainPresenter.PostStatusObserver.class, invocation.getArgument(1).getClass());

            // Check that the Status has the right post content
            assertEquals(status_content, invocation.getArgument(0, Status.class).getPost());
        }
    }

    private class ObserverSuccessAnswer extends ObserverAnswer {
        @Override
        protected void handleCase(MainPresenter.PostStatusObserver observer) {
            observer.handleSuccess();
        }
    }

    private class ObserverFailureAnswer extends ObserverAnswer {
        @Override
        protected void handleCase(MainPresenter.PostStatusObserver observer) {
            observer.handleFailure(errorMessage);
        }
    }

    private class ObserverExceptionAnswer extends ObserverAnswer {
        @Override
        protected void handleCase(MainPresenter.PostStatusObserver observer) {
            observer.handleException(new Exception(errorMessage));
        }
    }
}
