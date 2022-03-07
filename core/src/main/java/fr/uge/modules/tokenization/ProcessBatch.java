package fr.uge.modules.tokenization;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessBatch {
    private final PreparedStatement preparedLog;
    private final PreparedStatement preparedToken;
    private final Logger logger = Logger.getGlobal();
    private long timeBetweenBatch;
    private final static int BATCH_SIZE = 10;

    public ProcessBatch() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://timescale:5432/rootcause?user=root&password=root&stringtype=unspecified");
        preparedLog = connection.prepareStatement("INSERT INTO log(id,datetime,autogenerateddatetime) VALUES (?, ?, ?)");
        preparedToken = connection.prepareStatement("INSERT INTO token (idlog, idtokentype, value) VALUES (?, ?, ?)");
    }

    public void addInLogBatch(Long idlog, Timestamp datetime, Boolean autogenerateddatetime) {
        try {
            preparedLog.setLong(1, idlog);
            preparedLog.setTimestamp(2, datetime);
            preparedLog.setBoolean(3, autogenerateddatetime);
            preparedLog.addBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while adding in batch", e);
        }
    }

    public void addInTokenBatch(Long idlog, int idtokentype, String value) {
        try {
            preparedToken.setLong(1, idlog);
            preparedToken.setInt(2, idtokentype);
            preparedToken.setString(3, value);
            preparedToken.addBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while adding in batch", e);
        }
    }

    public void execute() {
        try {
            System.out.println("TIMER "  +(System.currentTimeMillis() - timeBetweenBatch));
            if((System.currentTimeMillis() - timeBetweenBatch) >= 3000) {
                System.out.println("BATCH EXECUTED ");
                preparedLog.executeBatch();
                preparedToken.executeBatch();
                timeBetweenBatch = System.currentTimeMillis();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}