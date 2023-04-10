package edu.byu.cs.tweeter.server.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class SQSAccessor {
    private static final String POST_STATUS_URL = "https://sqs.us-east-2.amazonaws.com/618333887325/PostStatusQueue";
    private static final String UPDATE_FEED_URL = "https://sqs.us-east-2.amazonaws.com/618333887325/UpdateFeedQueue";

    public static SendMessageResult sendPostStatusMessage(String messageBody) {
        return sendQueueMessage(messageBody, POST_STATUS_URL);
    }

    public static SendMessageResult sendUpdateFeedMessage(String messageBody) {
        return sendQueueMessage(messageBody, UPDATE_FEED_URL);
    }

    private static SendMessageResult sendQueueMessage(String messageBody, String url) {
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(url)
                .withMessageBody(messageBody);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

        // TODO What else do I need to do with message result?
        String msgId = send_msg_result.getMessageId();
        System.out.println("Message ID: " + msgId);

        return send_msg_result;
    }
}
