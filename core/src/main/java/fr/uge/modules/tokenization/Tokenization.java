package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.entities.Token;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//@ApplicationScoped
public class Tokenization {

    public Token tokenizeLog(long id, String body) {
/*        Objects.requireNonNull(body);
        // Containing the regex
        List<TokenType> tokenTypes = new ArrayList<>();
        TypeDate typeDate = new TypeDate();
        TypeTime typeTime = new TypeTime();
        tokenTypes.add(typeDate);
        tokenTypes.add(typeTime);
        tokenTypes.add(new TypeIPv4());
        tokenTypes.add(new TypeHTTPStatus());
        // Containing the token values
        List<Token> tokens = new ArrayList<>();

        String datetime = null;

        for (String word : body.split("\t")) {
            tokenTypes.stream().filter(tokenType -> word.matches(tokenType.getRegex()))
                    .forEach(tokenType -> tokens.add(new Token(word, tokenType)));
        }
        Optional<Token> date = tokens.stream().filter(token -> token.getType() == typeDate).findFirst();
        Optional<Token> time = tokens.stream().filter(token -> token.getType() == typeTime).findFirst();

        if (date.isPresent() && time.isPresent()) {
            datetime = date.get().getValue() + " " + time.get().getValue();
            Log.info(datetime);
        } else {
            Log.info("No DateTime : Autogenerate");
        }

        Log.info("Tokenize " + tokens);
        try {
            return new Tokens(id,
                    convertStringToTimestamp(datetime, "yyyy-MM-dd HH:mm:ss"), false,
                    tokens.stream()
                            .map(token -> new TokenModel(token.getType().getName() + "",
                                    token.getValue() + "")).toList());
        } catch (ParseException | NullPointerException e) {
            Log.warn("Incorrect value of datetime while parsing " + e);
            return new Tokens(id,
                    new Timestamp(Date.from(Instant.now()).getTime()), true,
                    tokens.stream()
                            .map(token -> new TokenModel(token.getType().getName() + "",
                                    token.getValue() + "")).toList());
        }
   */return null;
    }

    public static Timestamp convertStringToTimestamp(String strDate, String pattern) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date date = formatter.parse(strDate);
        return new Timestamp(date.getTime());
            }

    public static Timestamp convertStringToTimestamp(String strDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat();
        Date date = formatter.parse(strDate);
        return new Timestamp(date.getTime());
    }
}
