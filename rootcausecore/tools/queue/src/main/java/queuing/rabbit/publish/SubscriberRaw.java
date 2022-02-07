package queuing.rabbit.publish;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class SubscriberRaw {
    private final static String QUEUE_NAME = "logRaw";

    public static void main(String[] argv) throws Exception {
        SubscriberToken.runSubscriber(QUEUE_NAME);
    }
}