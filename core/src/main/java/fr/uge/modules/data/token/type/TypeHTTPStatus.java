package fr.uge.modules.data.token.type;

import fr.uge.modules.data.token.Token;

public class TypeHTTPStatus implements TokenType{

    private final String name = "status";
    private final String regex = "([1-5][0-5][0-9])";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRegex() {
        return regex;
    }

    @Override
    public float computeProximity(Token t1, Token t2) {
        if(t1.getValue().equals(t2.getValue())){
            return 100;
        } else if(t1.getValue().startsWith(t2.getValue().substring(0,1))){
            return 80;
        } else {
            return 0;
        }
    }

}
