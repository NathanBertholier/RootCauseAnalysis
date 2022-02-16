package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.TokenModel;

import java.util.Optional;

public class TypeTime implements TokenType{

    private final String name = "time";
    private final String regex = "([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])";

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
        return 0;
    }

    @Override
    public float computeProximity(TokenModel t1, TokenModel t2) {
        return 0;
    }

}
