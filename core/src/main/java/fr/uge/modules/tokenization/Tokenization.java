package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.linking.token.type.*;
import io.quarkus.logging.Log;
import org.jboss.logging.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class Tokenization {
    private final List<TokenType> tokenTypes = new ArrayList<>();
    private final TypeTime time = new TypeTime();
    private final TypeDate date = new TypeDate();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Tokenization() {
        tokenTypes.add(new TypeIPv4());
        tokenTypes.add(new TypeHTTPStatus());
        tokenTypes.add(new TypeEdgeResponse());
    }

    public LogEntity tokenizeLog(long id, String body) {
        Objects.requireNonNull(body);
        // Containing the token values
        List<TokenEntity> tokens = new ArrayList<>();

        String dateString = "";
        String timeString = "";

        for (String word : body.split("\t")) {
            if(date.matcher(word) != -1) {
                dateString = word;
            } else if(time.matcher(word) != -1) {
                timeString = word;
            } else {
                this.parseTokens(tokens, word);
            }
        }

        var log = new LogEntity();
        log.setId(id);
        log.setDatetime(convertStringToTimestamp(dateString + " " + timeString));
        log.setTokens(tokens);
        return log;
    }

    private void parseTokens(List<TokenEntity> tokenEntities, String word) {
        for(TokenType tokenType: tokenTypes){
            var type = tokenType.matcher(word);
            if(type != -1){
                TokenEntity token = new TokenEntity();
                token.setIdtokentype(type);
                token.setValue(word);
                tokenEntities.add(token);
            }
        }
    }

    public Timestamp convertStringToTimestamp(String strDate) {
        try {
            return new Timestamp(this.formatter.parse(strDate).getTime());
        } catch (ParseException e) {
            Log.log(Logger.Level.WARN, "Incorrect timestamp ", e);
        }
        return Timestamp.from(Instant.now());
    }
}
