package edu.byu.cs.tweeter.server.sqs;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class SQSAccessor {
    private static final String POST_STATUS_URL = "https://sqs.us-east-2.amazonaws.com/618333887325/PostStatusQueue";
    private static final String UPDATE_FEED_URL = "https://sqs.us-east-2.amazonaws.com/618333887325/UpdateFeedQueue";
    private static AmazonSQS sqs;

    public static SendMessageResult sendPostStatusMessage(String messageBody) {
        System.out.println("Sending post status message");
        return sendQueueMessage(messageBody, POST_STATUS_URL);
    }

    public static SendMessageResult sendUpdateFeedMessage(String messageBody) {
        return sendQueueMessage(messageBody, UPDATE_FEED_URL);
    }

    public static void deletePostStatusMessage(SQSEvent.SQSMessage msg) {
        deleteMessage(POST_STATUS_URL, msg);
    }

    public static void deleteUpdateFeedMessage(SQSEvent.SQSMessage msg) {
        deleteMessage(UPDATE_FEED_URL, msg);
    }

    private static void deleteMessage(String url, SQSEvent.SQSMessage msg) {
        getClient().deleteMessage(url, msg.getReceiptHandle());
    }

    private static SendMessageResult sendQueueMessage(String messageBody, String url) {
        try {
            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(url)
                    .withMessageBody(messageBody);

            SendMessageResult send_msg_result = getClient().sendMessage(send_msg_request);

            // TODO What else do I need to do with message result?
            String msgId = send_msg_result.getMessageId();
            System.out.println("Message ID: " + msgId);

            return send_msg_result;
        }
        catch(Exception ex) {
            throw new RuntimeException("[Server Error] Could not access queue: " + url);
        }
    }

    private static AmazonSQS getClient() {
        System.out.println("Getting client");
        if(sqs == null) {
            System.out.println("Making a new client");
            sqs = AmazonSQSClientBuilder.defaultClient();
        }

        return sqs;
    }
}
