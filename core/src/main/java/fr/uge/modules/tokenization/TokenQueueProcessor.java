package fr.uge.modules.tokenization;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.RawLogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class TokenQueueProcessor {
    public static void main(String[] args) throws IOException, TimeoutException, SQLException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        Tokenization tokenization = new Tokenization();
        java.sql.Connection dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/rootcause?user=root&password=root&stringtype=unspecified");
        PreparedStatement preparedLog = dbConnection.prepareStatement("INSERT INTO log(id,datetime,autogenerateddatetime) VALUES (?, ?, ?)");
        PreparedStatement preparedToken = dbConnection.prepareStatement("INSERT INTO token (idlog, idtokentype, value) VALUES (?, ?, ?)");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                String message = new String(delivery.getBody(), "UTF-8");
                RawLogEntity rawLogEntity = new JsonObject(message).mapTo(RawLogEntity.class);
                LogEntity logEntity = tokenization.tokenizeLog(rawLogEntity.id, rawLogEntity.log);
                preparedLog.setLong(1, logEntity.id);
                preparedLog.setTimestamp(2, logEntity.datetime);
                preparedLog.setBoolean(3, logEntity.autogeneratedDatetime);
                for (TokenEntity token : logEntity.tokens) {
                    preparedToken.setLong(1, token.idlog);
                    preparedToken.setLong(2, token.idtokentype);
                    preparedToken.setString(3, token.value);
                }

                preparedLog.execute();
                preparedToken.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        channel.basicConsume("token", true, deliverCallback, consumerTag -> {
        });
    }
}
