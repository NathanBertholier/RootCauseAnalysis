package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.TokenModel;
import fr.uge.modules.api.model.Tokens;
import fr.uge.modules.linking.token.Token;
import fr.uge.modules.linking.token.type.*;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@ApplicationScoped
public class Tokenization {
    private final TypeDate patternDate = new TypeDate();
    private final TypeTime patternTime = new TypeTime();
    private final TypeDatetime patternDatetime = new TypeDatetime();
    private final TypeIPv4 patternIP = new TypeIPv4();
    private final TypeHTTPStatus patternStatus = new TypeHTTPStatus();
    private static final Logger LOGGER = Logger.getGlobal();

    // TODO Transform to field

    public Tokens tokenizeLog(long id, String body){
        Objects.requireNonNull(body);
        // Containing the regex

        // Containing the token values
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

        var newTokens = new Tokens();
        newTokens.typeId = id;
        newTokens.timestamp = convertStringToTimestamp(datetime.toString(), "yyyy-MM-dd");
        newTokens.tokens = tokens.stream()
                .map(token -> new TokenModel(token.getType().getName() + "", token.getValue() + ""))
                .toList();
        return newTokens;
    }

    public static Timestamp convertStringToTimestamp(String strDate, String pattern) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            Date date = formatter.parse(strDate);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            LOGGER.severe("Incorrect value of datetime while parsing " + e);
            return null;
        }
    }
}