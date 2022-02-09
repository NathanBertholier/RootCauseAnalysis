package fr.uge.db.insert.logtoken;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class LogInserter {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Properties PROPERTIES = new Properties();
    private final Connection conn;
    private final PreparedStatement insertStatement;

    public LogInserter() throws SQLException {
        try {
            PROPERTIES.load(LogInserter.class.getClassLoader().getResourceAsStream("init.properties"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"IOException",e);
        }
        this.conn = DriverManager.getConnection("jdbc:postgresql://" +
                PROPERTIES.getProperty("DBSRV") +
                ":5432/" +
                PROPERTIES.getProperty("DB") +
                "?user=" +
                PROPERTIES.getProperty("DBLOGIN") +
                "&password=" +
                PROPERTIES.getProperty("DBPWD") +
                "&stringtype=unspecified");
        this.insertStatement = this.conn.prepareStatement("INSERT INTO rawlog (id,value) VALUES (?,?)");
    }

    public void insertInMonitoring(long id, String val) throws SQLException {
        this.insertStatement.setLong(1, id);
        this.insertStatement.setString(2, val);
        this.insertStatement.executeUpdate();
    }

    @PreDestroy
    void destroy() throws SQLException {
        this.conn.close();
    }
}
