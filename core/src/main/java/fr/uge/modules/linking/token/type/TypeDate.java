package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.Token;

public class TypeDate implements TokenType{

    private final String name = "date";
    private final String regex = "((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";

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
        return 1;
    }

    @Override
    public float computeProximity(Token t1, Token t2) {
        return 0;
    }

}
