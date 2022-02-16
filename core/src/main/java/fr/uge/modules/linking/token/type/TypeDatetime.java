package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TypeDatetime implements TokenType{

    private final String name = "datetime";
    private final String regex = "((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])\t([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])";

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
        return 4;
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

    public static float computeDateTimeProximity(LocalDateTime ldt1, LocalDateTime ldt2, float delta){
        return  1 - (Math.abs(ChronoUnit.SECONDS.between(ldt1,ldt2))/delta);
    }

}
