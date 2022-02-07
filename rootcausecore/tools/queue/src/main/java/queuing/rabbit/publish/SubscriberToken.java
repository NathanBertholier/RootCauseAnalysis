package queuing.rabbit.publish;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class SubscriberToken {
    private final static String QUEUE_NAME = "logTokenization";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        final var connection = factory.newConnection();
        final var channel = connection.createChannel();

        channel.exchangeDeclare(QUEUE_NAME, "fanout", true, false, null);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> System.out.println("ID : "  + delivery.getProperties().getHeaders().get("id") + new String(delivery.getBody(), StandardCharsets.UTF_8));
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}