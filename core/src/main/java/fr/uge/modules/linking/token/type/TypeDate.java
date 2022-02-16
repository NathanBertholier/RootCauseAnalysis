package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.List;

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
    public int matcher(String word) {
        if(word.matches(regex)){
            return 0;
        }
        return -1;
    }

    @Override
    public float computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        return 0;
    }

}
