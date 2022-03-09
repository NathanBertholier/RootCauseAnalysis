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

/**
 * Class used to perform batch operations
 */
public class BatchQueueProcessor {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final String QUEUE_NAME = "batch";

    /**
     * Start a main that read message in the queue QUEUE_NAME and add them in a batch using the ProcessBatch class.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Optional<Channel> channel = createChannel();
        while (channel.isEmpty()){
            LOGGER.log(Level.WARNING,"Channel not connected, retry connecting in 5 seconds");
            Thread.sleep(5000);
            channel = createChannel();
        }
        ProcessBatch processBatch = null;
        while (processBatch == null) {
            try {
                processBatch = new ProcessBatch();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING,"Database not connected, retry connecting in 5 seconds");
                Thread.sleep(5000);
            }
        }

        processBatch.batchRunnable();

        LOGGER.log(Level.INFO, "Channel started and waiting for message.");
        channel.orElseThrow().basicConsume(QUEUE_NAME, true, getCallBack(processBatch), consumerTag -> {
        });
    }

    /**
     * Create a new channel to connect to RabbitMQ queue
     * @return Optional of Channel if exists, Optional.Empty if an error append.
     */
    private static Optional<Channel> createChannel() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("rabbitmq");
            Connection connection = factory.newConnection();
            return Optional.of(connection.createChannel());
        } catch (IOException | TimeoutException e) {
            return Optional.empty();
        }
    }

    /**
     * Create the function used for each message read in queue
     * @param processBatch The object used to add in database as a batch
     * @return DeliverCallBack representing the function
     */
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
