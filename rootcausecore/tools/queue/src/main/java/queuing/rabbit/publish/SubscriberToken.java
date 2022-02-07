package queuing.rabbit.publish;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class SubscriberToken {
    private final static String QUEUE_NAME = "logTokenization";

    public static void main(String[] argv) throws Exception {
        runSubscriber(QUEUE_NAME);
    }

    static void runSubscriber(String queueName) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (var connection = factory.newConnection()) {
            try (var channel = connection.createChannel()) {

                channel.exchangeDeclare(queueName, "fanout", true, false, null);

                System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
                DeliverCallback deliverCallback = (consumerTag, delivery) -> System.out.println("ID : " + delivery.getProperties().getHeaders().get("id") + new String(delivery.getBody(), StandardCharsets.UTF_8));
                channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
                });
            }
        }
    }
}