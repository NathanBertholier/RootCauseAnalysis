package fr.uge.db.insert.logtoken;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
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
    private static final  Object signal = new Object();
    private static boolean wasSignalled = false;
    private static final SecureRandom rand = new SecureRandom();
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Properties PROPERTIES = new Properties();
    static {
        try {
            PROPERTIES.load(LogInserter.class.getClassLoader().getResourceAsStream("init.properties"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"IOException",e);
        }
    }

    public static void main(String[] args) throws SQLException {
        var conn = DriverManager.getConnection("jdbc:postgresql://"+PROPERTIES.getProperty("DBSRV")+":5432/"+PROPERTIES.getProperty("DB")+"?user="+PROPERTIES.getProperty("DBLOGIN")+"&password="+PROPERTIES.getProperty("DBPWD")+"&stringtype=unspecified");
        try {
            getValueFromAPI(conn);
        } catch (IllegalStateException | IOException | TimeoutException error) {
            LOGGER.log(Level.SEVERE,"IllegalStateException | IOException | TimeoutException",error);
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
                        LOGGER.log(Level.WARNING,"SQLException",e);
                    }
                };
                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
            }
        }


    }

}
