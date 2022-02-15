package fr.uge.db.insert.monitoring;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;


//@ApplicationScoped
public class MonitorInserter {
    private static final String QUEUE_NAME = "logs";
    private final Properties properties = new Properties();
    private final Logger logger = Logger.getGlobal();

    public MonitorInserter() throws SQLException, IOException {
    }

    private void insertInMonitoring(long confirm, long publish, float avg) {

    }
}
