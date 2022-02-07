package fr.umlv.token.type;

import fr.umlv.token.Token;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    public float computeProximity(Token t1, Token t2) {
        return 0;
    }

    public static float computeDateTimeProximity(LocalDateTime ldt1, LocalDateTime ldt2, float delta){
        return  1 - (Math.abs(ChronoUnit.SECONDS.between(ldt1,ldt2))/delta);
    }

}
