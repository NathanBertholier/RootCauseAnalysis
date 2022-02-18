package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.List;

public class TypeDate implements TokenType{

    private static final String NAME = "date";
    private static final String REGEX = "([0-9]{4})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";

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
