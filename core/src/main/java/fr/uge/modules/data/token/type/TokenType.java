package fr.uge.modules.data.token.type;

import fr.uge.modules.data.token.Token;

public interface TokenType {

    String getName();
    String getRegex();
    float computeProximity(Token t1, Token t2);

    static TokenType fromTokenTypeId(int tokentypeId){
        return switch(tokentypeId){
            case 1 -> new TypeDatetime();
            default -> new TypeIPv4();
        };
    }

}
