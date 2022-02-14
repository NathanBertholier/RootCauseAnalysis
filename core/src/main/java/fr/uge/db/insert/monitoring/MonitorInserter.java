package fr.uge.db.insert.monitoring;

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


//@ApplicationScoped
public class MonitorInserter {
    private static final String QUEUE_NAME = "logs";
    private final Properties properties = new Properties();
    private final Logger logger = Logger.getGlobal();

    public MonitorInserter() throws SQLException, IOException {
        /*
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
         */
    }

    private void insertInMonitoring(long confirm, long publish, float avg) {
        /*
        try {
            this.preparedStatement.setTimestamp(1, Timestamp.from(Instant.now()));
            this.preparedStatement.setLong(2, confirm);
            this.preparedStatement.setLong(3, publish);
            this.preparedStatement.setFloat(4, avg);
            this.preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error while inserting in monitor database" + e);
        }
         */
    }
}
