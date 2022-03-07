package fr.uge.modules.queuing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import fr.uge.modules.api.model.entities.LogEntity;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BatchQueueProcessor {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final String QUEUE_NAME = "batch";

    public static void main(String[] args) throws IOException, TimeoutException, SQLException {
        Channel channel = createChannel().orElseThrow();
        ProcessBatch processBatch = new ProcessBatch();
        processBatch.batchRunnable();

        System.out.println(" [*] Waiting for LogEntity message. To exit press CTRL+C");
//        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            try {
//                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
//                LogEntity logEntity = new JsonObject(message).mapTo(LogEntity.class);
//                processBatch.addInBatch(logEntity);
//            } catch (Exception e) {
//                LOGGER.log(Level.SEVERE, "Error while inserting in database", e);
//            }
//        };
        channel.basicConsume(QUEUE_NAME, true, getCallBack(processBatch), consumerTag -> {
        });
    }

    private static Optional<Channel> createChannel() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            return Optional.of(connection.createChannel());
        } catch (IOException | TimeoutException e) {
            LOGGER.log(Level.SEVERE, "Error while creating channel", e);
            return Optional.empty();
        }
    }

    private static DeliverCallback getCallBack(ProcessBatch processBatch) {
        return (consumerTag, delivery) -> {
            try {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                LogEntity logEntity = new JsonObject(message).mapTo(LogEntity.class);
                processBatch.addInBatch(logEntity);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error while inserting in database", e);
            }
        };
    }
}
