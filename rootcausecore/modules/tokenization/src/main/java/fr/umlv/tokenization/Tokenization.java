package fr.umlv.tokenization;

import fr.umlv.token.Token;
import fr.umlv.token.type.TypeDate;
import fr.umlv.token.type.TypeDatetime;
import fr.umlv.token.type.TypeIPv4;
import fr.umlv.token.type.TypeTime;

import java.util.*;

public class Tokenization {

    private static String[] splitter(String body, String delimitor){
        return body.split(delimitor);
    }

    public Map<String,String> tokenizeLog(String body){
        Objects.requireNonNull(body);
        // Containing the regex
        TypeDate patternDate = new TypeDate();
        TypeTime patternTime = new TypeTime();
        TypeDatetime patternDatetime = new TypeDatetime();
        TypeIPv4 patternIP = new TypeIPv4();
        // Containing the token values
        Token tokenDate = new Token(patternDate);
        Token tokenTime = new Token(patternTime);
        Token tokenDatetime = new Token(patternDatetime);
        Token tokenIP = new Token(patternIP);
        List<Token> tokens = Arrays.asList(/*tokenDate,tokenTime,*/tokenDatetime,tokenIP);

        Map<String,String> res = new HashMap<>();
        StringBuilder datetime = new StringBuilder();
        for(String word : splitter(body,"\t")){
            if(word.matches(patternDate.getRegex())) {
                //System.out.println("date : " + word);
                datetime.append(word);
                //tokenDate.setValue(word);
            } else if(word.matches(patternTime.getRegex())) {
                //System.out.println("time : " + word);
                datetime.append(" ").append(word);
                //tokenTime.setValue(word);
            } else if(word.matches(patternIP.getRegex())) {
                //System.out.println("IP : " + word);
                tokenIP.setValue(word);
            }
        }
        tokenDatetime.setValue(datetime.toString());
        for(Token token: tokens){
            res.put(token.getType().getName(), token.getValue());
        }
        return res;
    }

}
