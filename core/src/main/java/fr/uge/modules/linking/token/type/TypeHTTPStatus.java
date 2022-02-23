package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.List;

public class TypeHTTPStatus implements TokenType{

    private static final String NAME = "status";
    private static final String REGEX = "([1-5][0-5][0-9])";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getRegex() {
        return REGEX;
    }

    @Override
    public Integer getTokenTypeId() {
        return TokenTypeId.ID_STATUS.getId();
    }

    @Override
    public float computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        return 0;
        /*var leftVal = tokenLeft.getValue();
        var rightVal = tokenRight.getValue();
        if(leftVal.equals(rightVal)) {
            return 100;
        } else if(leftVal.startsWith(rightVal.substring(0,1))){
            return 80;
        } else {
            return 0;
        }*/
    }

}
