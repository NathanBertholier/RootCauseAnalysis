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

/**
 * Hello world!
 *
 */
public class LogInserter
{
    private final static String QUEUE_NAME = "log";
    private final static String CONN_URL = "jdbc:postgresql://localhost:5432/rootcause?user=root&password=root&stringtype=unspecified";
    private final static Object signal = new Object();
    private static boolean wasSignalled = false;

    public static void main(String[] args) throws SQLException {
        var conn = DriverManager.getConnection(CONN_URL);
        try {
            getValueFromAPI(conn);
        } catch (IllegalStateException | IOException | TimeoutException error) {
            error.printStackTrace();
        }
        synchronized (signal){
            if(!wasSignalled){
                try {
                    signal.wait();
                } catch (InterruptedException e){
                    throw new AssertionError();
                }
            }
        }
    }

    private static void insertInMonitoring(int id, String val, Connection conn) throws SQLException {

        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO rawlog (id,value) VALUES (?,?)");
        preparedStatement.setInt(1,id);
        preparedStatement.setString(2, val);
        preparedStatement.executeUpdate();
    }

    private static void getValueFromAPI(Connection conn) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        com.rabbitmq.client.Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
            var rand = new Random();
            try {
                insertInMonitoring(rand.nextInt(0,999999), message, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }

}
