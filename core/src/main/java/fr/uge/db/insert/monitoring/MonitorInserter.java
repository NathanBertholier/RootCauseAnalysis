package fr.uge.db.insert.monitoring;

import com.google.gson.JsonParser;
import io.quarkus.scheduler.Scheduled;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Logger;


@ApplicationScoped
public class MonitorInserter {
    private static final String QUEUE_NAME = "logs";
    private final Properties properties = new Properties();
    private final Logger logger = Logger.getGlobal();
    private final Connection connection;
    private final PreparedStatement preparedStatement;

    public MonitorInserter() throws SQLException, IOException {
        properties.load(MonitorInserter.class.getClassLoader().getResourceAsStream("init.properties"));
        this.connection = DriverManager.getConnection("jdbc:postgresql://" +
                properties.getProperty("DBSRV") +
                ":5432/" +
                properties.getProperty("DB") +
                "?user=" +
                properties.getProperty("DBLOGIN") +
                "&password=" +
                properties.getProperty("DBPWD") +
                "&stringtype=unspecified");
        this.preparedStatement = connection.prepareStatement("INSERT INTO monitoring (datetime,deliver,publish,avg_rate) VALUES (?,?,?,?)");
    }

    private void insertInMonitoring(long confirm, long publish, float avg) {
        try {
            this.preparedStatement.setTimestamp(1, Timestamp.from(Instant.now()));
            this.preparedStatement.setLong(2, confirm);
            this.preparedStatement.setLong(3, publish);
            this.preparedStatement.setFloat(4, avg);
            this.preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error while inserting in monitor database" + e);
        }
    }

    @Scheduled(every="5s")
    public void getValueFromAPI() {
        String sURL = "http://"
                + properties.getProperty("MQMONITORINGSRV")
                + ":15672/api/exchanges/%2f/"
                + QUEUE_NAME
                + "?msg_rates_age=60&msg_rates_incr=5";

        try {
            URL url = new URL(sURL);
            String userPass = "guest" + ":" + "guest";
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes());//or
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.connect();

            var json = JsonParser.parseReader(new InputStreamReader((InputStream) urlConnection.getContent())).getAsJsonObject();
            var jsonTmp = json.get("incoming").getAsJsonArray().get(0).getAsJsonObject().get("stats").getAsJsonObject();
            var confirm = jsonTmp.get("confirm").getAsLong();
            var publish = jsonTmp.get("publish").getAsLong();
            var avg = jsonTmp.get("confirm_details").getAsJsonObject().get("avg_rate").getAsFloat();

            insertInMonitoring(confirm, publish, avg);

        } catch (IOException e) {
            logger.severe("Error monitoring " + e);
        } catch (IndexOutOfBoundsException i) {
            logger.warning("Incorrect json schema, no data send yet.");
        }
    }

    @PreDestroy
    public void close() throws SQLException {
        this.connection.close();
    }
}
