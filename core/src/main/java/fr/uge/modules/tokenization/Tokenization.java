package fr.uge.modules.tokenization;

import fr.uge.modules.linking.token.Token;
import fr.uge.modules.linking.token.type.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class Tokenization {
    // TODO Transform to field

    public ArrayList<Token> tokenizeLog(String body){
        Objects.requireNonNull(body);
        // Containing the regex
        TypeDate patternDate = new TypeDate();
        TypeTime patternTime = new TypeTime();
        TypeDatetime patternDatetime = new TypeDatetime();
        TypeIPv4 patternIP = new TypeIPv4();
        TypeHTTPStatus patternStatus = new TypeHTTPStatus();
        // Containing the token values
        Token tokenDatetime = new Token(patternDatetime);
        Token tokenIP = new Token(patternIP);
        Token tokenStatus = new Token(patternStatus);
        ArrayList<Token> tokens = new ArrayList<>(Arrays.asList(tokenDatetime,tokenIP, tokenStatus));

        StringBuilder datetime = new StringBuilder();
        for(String word : body.split("\t")){
            if(word.matches(patternDate.getRegex())) {
                datetime.append(word);
            } else if(word.matches(patternTime.getRegex())) {
                datetime.append(" ").append(word);
            } else if(word.matches(patternIP.getRegex())) {
                tokenIP.setValue(word);
            } else if(word.matches(patternStatus.getRegex())){
                tokenStatus.setValue(word);
            }
        }
        tokenDatetime.setValue(datetime.toString());
        return tokens;
    }
}