package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.entities.Log;
import fr.uge.modules.linking.token.Token;
import fr.uge.modules.linking.token.type.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//@ApplicationScoped
public class Tokenization {
    private final TypeDate patternDate = new TypeDate();
    private final TypeTime patternTime = new TypeTime();
    private final TypeDatetime patternDatetime = new TypeDatetime();
    private final TypeIPv4 patternIP = new TypeIPv4();
    private final TypeHTTPStatus patternStatus = new TypeHTTPStatus();
    private static final Logger LOGGER = Logger.getGlobal();


    public Log tokenizeLog(long id, String body) {
        Objects.requireNonNull(body);
        // Containing the regex
        Token tokenIP = new Token(this.patternIP);
        Token tokenStatus = new Token(this.patternStatus);
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(tokenIP);
        tokens.add(tokenStatus);

        StringBuilder datetime = new StringBuilder();
        for(String word : body.split("\t")){
            if(word.matches(this.patternDate.getRegex())) {
                datetime.append(word);
            } else if(word.matches(this.patternTime.getRegex())) {
                datetime.append(" ").append(word);
            } else if(word.matches(this.patternIP.getRegex())) {
                tokenIP.setValue(word);
            } else if(word.matches(patternStatus.getRegex())){
                tokenStatus.setValue(word);
            }
        }

        var log = new Log();
        log.id = id;
        log.datetime = convertStringToTimestamp(datetime.toString(), "yyyy-MM-dd");
        log.tokens = tokens.stream()
                .map(token -> {
                    var tokenConstruct = new fr.uge.modules.api.model.entities.Token();
                    tokenConstruct.idtokentype = token.getType().getTokenTypeId();
                    tokenConstruct.value = token.getValue() + "";
                    return tokenConstruct;
                }).toList();
        return log;
    }

    public static Timestamp convertStringToTimestamp(String strDate, String pattern) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            Date date = formatter.parse(strDate);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            LOGGER.log(Level.WARNING, "Incorrect timestamp ",e);
        }
        return Timestamp.from(Instant.now());
    }

    public static Timestamp convertStringToTimestamp(String strDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat();
        Date date = formatter.parse(strDate);
        return new Timestamp(date.getTime());
    }
}
