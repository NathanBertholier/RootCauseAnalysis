package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.TokenModel;

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
    public Integer getTokenTypeId() {
        return 3;
    }

    @Override
    public int matcher(String word) {
        if(word.matches(regex)){
            return TokenTypeId.ID_STATUS;
        }
        return -1;
    }

    @Override
    public float computeProximity(TokenModel t1, TokenModel t2) {
        if(t1.token_value().equals(t2.token_value())){
            return 100;
        } else if(t1.token_value().startsWith(t2.token_value().substring(0,1))){
            return 80;
        } else {
            return 0;
        }
    }

}
