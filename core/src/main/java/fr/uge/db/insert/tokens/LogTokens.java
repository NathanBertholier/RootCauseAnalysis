package fr.uge.db.insert.tokens;

import fr.uge.db.insert.log.LogInserter;
import fr.uge.modules.api.model.TokenModel;
import fr.uge.modules.api.model.Tokens;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class LogTokens {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Properties PROPERTIES = new Properties();
    private final Connection conn;
    private final PreparedStatement logStatement;
    private final PreparedStatement tokenStatement;

    public LogTokens() throws SQLException {
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
        this.tokenStatement = this.conn.prepareStatement("INSERT INTO token (idlog, idtokentype, value) VALUES (?,?,?)");

    }

    public void insertTokens(long id, Timestamp date, List<TokenModel> tokens) {
        try {
            this.logStatement.setLong(1, id);
            System.out.println(date);
            this.logStatement.setTimestamp(2, date);
            tokens.forEach(token -> {
                try {
                    var tokenId = switch (token.token_type()) {
                        case "IPv4"-> 1;
                        case "IPv6" -> 2;
                        case "status" -> 3;
                        case "datetime" -> 4;
                        case "edgeResponse" -> 5;
                        default -> {throw new IllegalArgumentException(token.token_type()+" not recognized");}
                    };
                    this.tokenStatement.setLong(1, id);
                    this.tokenStatement.setLong(2, tokenId);
                    this.tokenStatement.setString(3, token.token_value());
                    this.tokenStatement.execute();
                } catch (SQLException e) {
                    LOGGER.severe("Error during insertion in log and token database : " + e);
                }catch (IllegalArgumentException e){
                    LOGGER.log(Level.WARNING,"IllegalToken",e);
                }
            });
            this.logStatement.execute();
        } catch (SQLException e) {
            LOGGER.severe("Error during insertion in log and token database : " + e);
        }
    }


    @Incoming(value = "tokensOut")
    public void process(JsonObject incoming) {
        var tokens = incoming.mapTo(Tokens.class);
        this.insertTokens(tokens.id(), tokens.timestamp(),tokens.tokens());
    }

    @PreDestroy
    void destroy() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            LOGGER.severe("Error while closing the connection :" + e);
        }
    }
}
