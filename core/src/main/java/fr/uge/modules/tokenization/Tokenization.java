package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.entities.Log;
import fr.uge.modules.api.model.entities.Token;
import fr.uge.modules.linking.token.type.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tokenization {
    private static final Logger LOGGER = Logger.getGlobal();
    private final List<TokenType> tokenTypes = new ArrayList<>();
    private final TypeTime time = new TypeTime();
    private final TypeDate date = new TypeDate();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Tokenization() {
        tokenTypes.add(new TypeIPv4());
        tokenTypes.add(new TypeHTTPStatus());
        tokenTypes.add(new TypeEdgeResponse());
    }

    public Log tokenizeLog(long id, String body) {
        Objects.requireNonNull(body);
        // Containing the token values
        List<Token> tokens = new ArrayList<>();

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

        var log = new Log();
        log.setId(id);
        log.setDatetime(convertStringToTimestamp(dateString + " " + timeString));
        log.setTokens(tokens);
        System.out.println(log);
        return log;
    }

    private void parseTokens(List<Token> tokenEntities, String word) {
        for(TokenType tokenType: tokenTypes){
            var type = tokenType.matcher(word);
            if(type != -1){
                Token token = new Token();
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
            LOGGER.log(Level.WARNING, "Incorrect timestamp ",e);
        }
        return Timestamp.from(Instant.now());
    }
}
