package fr.uge.db.insert.monitoring;

import com.google.gson.JsonArray;
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
import java.util.concurrent.TimeoutException;

import com.google.gson.JsonElement;

/**
 * Hello world!
 *
 */
public class MonitorInserter
{
    private final static String QUEUE_NAME = "log";
    private final static String CONN_URL = "jdbc:postgresql://localhost:5432/rootcause?user=root&password=root&stringtype=unspecified";
    private final static Object signal = new Object();
    private static boolean wasSignalled = false;

    public static void main(String[] args) throws SQLException {
        var conn = DriverManager.getConnection(CONN_URL);
        try {
            while(true){
                getValueFromAPI(conn);
                Thread.sleep(5000);
            }
        } catch (IllegalStateException | IOException | TimeoutException | InterruptedException error) {
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

    private static void insertInMonitoring(float ingressRate, float egressRate, long queueSize, long messagePoolBytes, Connection conn) throws SQLException {

        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO monitoring (datetime,ingressRate,egressRate,queuedmessages,messagepoolbytes) VALUES (?,?,?,?,?)");
        preparedStatement.setTimestamp(1, Timestamp.from(Instant.now()));
        preparedStatement.setFloat(2, ingressRate);
        preparedStatement.setFloat(3, egressRate);
        preparedStatement.setLong(4, queueSize);
        preparedStatement.setLong(5, messagePoolBytes);
        preparedStatement.executeUpdate();
    }

    private static void getValueFromAPI(Connection conn) throws IOException, TimeoutException {
        String sURL = "http://localhost:15672/api/queues";
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
        array.forEach((element) -> {
            if(element.getAsJsonObject().get("name").getAsString().equals("log")){
                System.out.println("trouvé");
                long messagePoolBytes = element.getAsJsonObject().get("message_bytes").getAsLong();
                JsonObject backing_queue_status = element.getAsJsonObject().get("backing_queue_status").getAsJsonObject();
                float ingressRate = backing_queue_status.get("avg_ingress_rate").getAsFloat();
                float egressRate = backing_queue_status.get("avg_egress_rate").getAsFloat();
                long queueSize = backing_queue_status.get("len").getAsLong();
                try {
                    insertInMonitoring(ingressRate,egressRate,queueSize,messagePoolBytes, conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });
        System.out.println(jsonElement.getAsJsonArray().size());

    }
}
