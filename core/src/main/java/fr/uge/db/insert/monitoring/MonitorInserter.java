package fr.uge.db.insert.monitoring;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class MonitorInserter
{
    private static final  String QUEUE_NAME = "log";
    private static final  Object signal = new Object();
    private static boolean wasSignalled = false;
    private static final Properties PROPERTIES=new Properties();
    private static final Logger LOGGER = Logger.getGlobal();
    private static final boolean LOOP=true;
    static {
        try {
            PROPERTIES.load(MonitorInserter.class.getClassLoader().getResourceAsStream("init.properties"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"IOException",e);
        }
    }
    public static void main(String[] args) throws SQLException {
        try (var conn = DriverManager.getConnection("jdbc:postgresql://" +
                PROPERTIES.getProperty("DBSRV") +
                ":5432/" +
                PROPERTIES.getProperty("DB") +
                "?user=" +
                PROPERTIES.getProperty("DBLOGIN") +
                "&password=" +
                PROPERTIES.getProperty("DBPWD") +
                "&stringtype=unspecified")) {
            try {
                while (LOOP) {
                    getValueFromAPI(conn);
                    Thread.sleep(5000);
                }
            } catch (IllegalStateException | IOException | TimeoutException | InterruptedException error) {
                LOGGER.log(Level.SEVERE,"IllegalStateException | IOException | TimeoutException | InterruptedException",error);
                Thread.currentThread().interrupt();
            }
        }
        synchronized (signal){
            while(!wasSignalled){
                try {
                    signal.wait();
                } catch (InterruptedException e){
                    LOGGER.log(Level.SEVERE,"InterruptedException",e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static void insertInMonitoring(float ingressRate, float egressRate, long queueSize, long messagePoolBytes, Connection conn) throws SQLException {

        try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO monitoring (datetime,ingressRate,egressRate,queuedmessages,messagepoolbytes) VALUES (?,?,?,?,?)")) {
            preparedStatement.setTimestamp(1, Timestamp.from(Instant.now()));
            preparedStatement.setFloat(2, ingressRate);
            preparedStatement.setFloat(3, egressRate);
            preparedStatement.setLong(4, queueSize);
            preparedStatement.setLong(5, messagePoolBytes);
            preparedStatement.executeUpdate();
        }
    }

    private static void getValueFromAPI(Connection conn) throws IOException, TimeoutException {
        String sURL = "http://"+PROPERTIES.getProperty("MQMONITORINGSRV")+":15672/api/queues";
        URL url = new URL(sURL);
        String userPass = "guest" + ":" + "guest";
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes());//or
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Authorization", basicAuth);
        try {
            urlConnection.connect();
        } catch (IOException e) {
            throw new IllegalStateException("RabbitMQ WebServer not started");
        }
        JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream) urlConnection.getContent()));
        JsonArray array = jsonElement.getAsJsonArray();
        array.forEach(element -> {
            if(element.getAsJsonObject().get("name").getAsString().equals(QUEUE_NAME)){
                long messagePoolBytes = element.getAsJsonObject().get("message_bytes").getAsLong();
                JsonObject backingQueueStatus = element.getAsJsonObject().get("backing_queue_status").getAsJsonObject();
                float ingressRate = backingQueueStatus.get("avg_ingress_rate").getAsFloat();
                float egressRate = backingQueueStatus.get("avg_egress_rate").getAsFloat();
                long queueSize = backingQueueStatus.get("len").getAsLong();
                try {
                    insertInMonitoring(ingressRate,egressRate,queueSize,messagePoolBytes, conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
