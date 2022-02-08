package fr.uge.db.insert.logtoken;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class LogInserter
{
    private static final  String QUEUE_NAME = "log";
    private static final  String CONN_URL = "jdbc:postgresql://localhost:5432/rootcause?user=root&password=root&stringtype=unspecified";
    private static final  Object signal = new Object();
    private static boolean wasSignalled = false;
    private static final Random rand = new Random();
    private static final Logger LOGGER = Logger.getGlobal();

    public static void main(String[] args) throws SQLException {
        var conn = DriverManager.getConnection(CONN_URL);
        try {
            getValueFromAPI(conn);
        } catch (IllegalStateException | IOException | TimeoutException error) {
            error.printStackTrace();
        }
        synchronized (signal){
            while(!wasSignalled){
                try {
                    signal.wait();
                } catch (InterruptedException e){
                    LOGGER.log(Level.WARNING,"InterruptedException",e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static void insertInMonitoring(int id, String val, Connection conn) throws SQLException {

        try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO rawlog (id,value) VALUES (?,?)")) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, val);
            preparedStatement.executeUpdate();
        }
    }

    private static void getValueFromAPI(Connection conn) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("localhost");
        try (com.rabbitmq.client.Connection connection = factory.newConnection()) {
            try (Channel channel = connection.createChannel()) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                LOGGER.log(Level.INFO," [*] Waiting for messages. To exit press CTRL+C");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    LOGGER.log(Level.INFO,() -> " [x] Received '" + message + "'");
                    try {
                        insertInMonitoring(rand.nextInt(0,999999), message, conn);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                };
                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
            }
        }


    }

}
