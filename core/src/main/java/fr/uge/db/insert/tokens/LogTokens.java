package fr.uge.db.insert.tokens;

import fr.uge.db.insert.log.LogInserter;
import fr.uge.modules.api.server.entities.Log;
import fr.uge.modules.api.server.entities.Token;
import fr.uge.modules.api.server.external.model.TokenModel;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class LogTokens {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Properties PROPERTIES = new Properties();
    /*private final Connection conn;
    private final PreparedStatement logStatement;
    private final PreparedStatement tokenStatement;*/

    @Inject
    EntityManager em;

    public LogTokens() {/*throws SQLException {
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
        this.logStatement = this.conn.prepareStatement("INSERT INTO log (id,datetime) VALUES (?,?)");
        this.tokenStatement = this.conn.prepareStatement("INSERT INTO token (idlog, idtokentype, value) VALUES (?,?,?)");*/

    }

    @Transactional
    public void insertTokens(long id, Date date, List<TokenModel> tokens) {
        Log log = new Log();
        log.setId(id);
        log.setDatetime(Timestamp.from(Instant.now()));

        tokens.forEach(token -> {
            Token to = new Token();
            to.setIdLog(log);
            to.setValue(token.token_value());
            to.setIdtokentype(switch (token.token_type()) {
                case "IPV4" -> 1;
                case "IPV6" -> 2;
                case "Statut" -> 3;
                case "Datetime" -> 4;
                case "EdgeResponse" -> 5;
                default -> 1;
            });
            em.persist(to);
        });
        em.persist(log);
        /*
        try {
            this.logStatement.setLong(1, id);
            this.logStatement.setTimestamp(2, Timestamp.from(Instant.now()));
            tokens.forEach(token -> {
                try {
                    var tokenId = switch (token.token_type()) {
                        case "IPV4"-> 1;
                        case "IPV6" -> 2;
                        case "Statut" -> 3;
                        case "Datetime" -> 4;
                        case "EdgeResponse" -> 5;
                        default -> 1;
                    };
                    this.tokenStatement.setLong(1, id);
                    this.tokenStatement.setLong(2, tokenId);
                    this.tokenStatement.setString(3, token.token_value());
                    this.tokenStatement.addBatch();
                } catch (SQLException e) {
                    LOGGER.severe("Error during insertion in log and token database : " + e);
                }
            });
            this.logStatement.execute();
            this.tokenStatement.execute();
        } catch (SQLException e) {
            LOGGER.severe("Error during insertion in log and token database : " + e);
        }*/
    }
}
