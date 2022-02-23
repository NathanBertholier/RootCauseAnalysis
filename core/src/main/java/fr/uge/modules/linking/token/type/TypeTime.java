package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.List;

public class TypeTime implements TokenType{

    private static final String NAME = "time";
    private static final String REGEX = "([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])(.([0-9])*){0,1}";

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
        return 0;
    }

    @Override
    public float computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        return 0;
    }

}
