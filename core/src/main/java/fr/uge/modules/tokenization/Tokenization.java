package fr.uge.modules.tokenization;

import fr.uge.modules.data.token.Token;
import fr.uge.modules.data.token.type.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class Tokenization {

    public ArrayList<Token> tokenizeLog(String body){
        Objects.requireNonNull(body);
        // Containing the regex
        TypeDate patternDate = new TypeDate();
        TypeTime patternTime = new TypeTime();
        TypeDatetime patternDatetime = new TypeDatetime();
        TypeIPv4 patternIP = new TypeIPv4();
        // Containing the token values
        Token tokenDatetime = new Token(patternDatetime);
        Token tokenIP = new Token(patternIP);
        ArrayList<Token> tokens = new ArrayList<>(Arrays.asList(tokenDatetime,tokenIP));

        StringBuilder datetime = new StringBuilder();
        for(String word : body.split("\t")){
            if(word.matches(patternDate.getRegex())) {
                datetime.append(word);
            } else if(word.matches(patternTime.getRegex())) {
                datetime.append(" ").append(word);
            } else if(word.matches(patternIP.getRegex())) {
                tokenIP.setValue(word);
            }
        }
        tokenDatetime.setValue(datetime.toString());
        return tokens;
    }
}