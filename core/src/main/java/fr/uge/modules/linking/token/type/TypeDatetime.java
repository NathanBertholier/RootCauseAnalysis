package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

public class TypeDatetime implements TokenType{

    private static final String NAME = "datetime";
    private static final String REGEX = "((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])\t([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])";
    private static final Random RANDOM = new SecureRandom();

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
        return TokenTypeId.ID_DATETIME.getId();
    }

    @Override
    public double computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        return 0;
    }

    public static float computeDateTimeProximity(Timestamp ldt1, Timestamp ldt2, float delta){
        /*
        var time = ChronoUnit.SECONDS.between(ldt1, ldt2);
        if(time > delta) {
            return 0;
        } else if(time == 0) {
            return 100;
        }
        return  1 - (time/delta);
         */
        return RANDOM.nextFloat();
    }

}
