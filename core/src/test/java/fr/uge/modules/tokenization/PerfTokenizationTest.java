package fr.uge.modules.tokenization;

import org.junit.jupiter.api.RepeatedTest;

import java.io.IOException;
import java.sql.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PerfTokenizationTest {
    private static  Connection connection;
    private static final Logger LOGGER = Logger.getLogger("Rootcause");
    private Tokenization tokenization = new Tokenization();

    static {
        try {
            FileHandler fh;
            fh = new FileHandler("perf.log",false);
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            connection = DriverManager.getConnection("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'src/test/resources/heavydata.sql'");
        } catch (SQLException | IOException e) {
            LOGGER.log(Level.SEVERE,"SQLException | IOException",e);
        }
    }
    
    @RepeatedTest(value=5)
    void benchmark() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rawlog");
        final ResultSet resultSet = preparedStatement.executeQuery();
        long start = System.currentTimeMillis();
        while (resultSet.next()) {
            assertEquals(resultSet.getString(2), tokenization.tokenizeLog(0, resultSet.getString(1)).toString());
        }
        long end = System.currentTimeMillis();
        LOGGER.log(Level.INFO,(end - start) + " MilliSeconds");
    }
}
