package fr.uge.modules.queuing;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;

import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessBatch {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final long BATCH_SECONDS = 3;

    private final PreparedStatement preparedLog;
    private final PreparedStatement preparedToken;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Create connection to database using jdbc and initiate statement for batch processing.
     * Statement will be used to add tokens and logs in database.
     */
    public ProcessBatch() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://timescale:5432/rootcause?user=root&password=root&stringtype=unspecified");
        preparedLog = connection.prepareStatement("INSERT INTO log(id,datetime,autogenerateddatetime) VALUES (?, ?, ?)");
        preparedToken = connection.prepareStatement("INSERT INTO token (idlog, idtokentype, value) VALUES (?, ?, ?)");
    }

    /**
     * Method called for each message in the queue.
     * It will first add the logEntity in the statement then each token.
     * @param logEntity Represent the Log found in the queue
     */
    public void addInBatch(LogEntity logEntity) {
        lock.lock();
        this.addInLogBatch(logEntity);
        logEntity.tokens.forEach(token -> addInTokenBatch(token, logEntity.id));
        lock.unlock();
    }

    /**
     * Add a "logEntity" in the log prepared query using his ID, datetime and the autogeneratedDatetime boolean.
     * @param logEntity Represent the logEntity found in the queue.
     */
    private void addInLogBatch(LogEntity logEntity) {
        try {
            preparedLog.setLong(1, logEntity.id);
            preparedLog.setTimestamp(2, logEntity.datetime);
            preparedLog.setBoolean(3, logEntity.autogeneratedDatetime);
            preparedLog.addBatch();
        } catch (SQLException e) {
            LOGGER.severe(() -> "Error while adding in batch: " + e);
        }
    }

    /**
     * Add a "token" in the token prepared query using the log ID, tokenType ID and the value.
     * @param token Represent the token to insert
     * @param id    Represent the id of log
     */
    private void addInTokenBatch(TokenEntity token, long id) {
        try {
            preparedToken.setLong(1, id);
            preparedToken.setInt(2, token.idtokentype);
            preparedToken.setString(3, token.value);
            preparedToken.addBatch();
        } catch (SQLException e) {
            LOGGER.severe(() -> "Error while adding in batch" + e);
        }
    }

    /**
     * Method called once the object was created.
     * It starts a thread called every BATCH_SECONDS that execute both statement.
     */
    public void batchRunnable() {
        try {
            Runnable batchRunnable = this::execute;
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(batchRunnable, 0, BATCH_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.severe(() -> "Exception: " + e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Method called every BATCH_SECONDS in another thread.
     * This method execute both prepared statement and insert them in the database.
     */
    private void execute() {
        lock.lock();
        try {
            preparedLog.executeBatch();
            preparedToken.executeBatch();
            LOGGER.log(Level.INFO, "Execute batch.");
        } catch (SQLException e) {
            LOGGER.severe(() -> "Error while executing the batch" + e);
        }
        lock.unlock();
    }
}