package fr.uge.db.insert.log;

import fr.uge.modules.api.server.entities.RawLog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.Properties;
import java.util.logging.Logger;

@ApplicationScoped
public class LogInserter {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Properties PROPERTIES = new Properties();
    /*private final Connection conn;
    private final PreparedStatement insertStatement;*/

    @Inject
    EntityManager em;

    public LogInserter() {/*throws SQLException {
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
        */
    }

    @Transactional
    public void insert(long id, String value) {
        RawLog rawLog = new RawLog();
        rawLog.setValue(value);
        rawLog.setId(id);
        em.persist(rawLog);
        /*try {
            this.insertStatement.setLong(1, id);
            this.insertStatement.setString(2, val);
            this.insertStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.severe("Error during insertion in Raw database : " + e);
        }*/
    }
}
