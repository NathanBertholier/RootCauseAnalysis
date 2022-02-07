package fr.umlv.token.type;

import fr.umlv.token.Token;

public interface TokenType {

    String getName();
    String getRegex();
    float computeProximity(Token t1, Token t2);

}
