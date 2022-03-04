package fr.uge.modules.tokenization;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import fr.uge.modules.api.model.entities.LogEntity;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class BatchQueueProcessor {
    public static void main(String[] args) throws IOException, TimeoutException, SQLException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        ProcessBatch processBatch = new ProcessBatch();

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                LogEntity logEntity = new JsonObject(message).mapTo(LogEntity.class);
                processBatch.addInLogBatch(logEntity.id, logEntity.datetime, logEntity.autogeneratedDatetime);
                logEntity.tokens.forEach(token -> processBatch.addInTokenBatch(logEntity.id, token.idtokentype, token.value));
                processBatch.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        channel.basicConsume("batch", true, deliverCallback, consumerTag -> {
        });
    }
}
