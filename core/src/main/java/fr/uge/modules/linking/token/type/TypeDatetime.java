package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.Link;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class TypeDatetime implements TokenType{

    private static final String NAME = "datetime";
    private static final String REGEX = "((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])\t([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])";

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

    public static Computation computeDateTimeProximity(Timestamp ldt1, Timestamp ldt2, float delta){
        var time = ldt1.getTime() - ldt2.getTime();
        return new Computation(new TypeDatetime(), ldt1.toString(), ldt2.toString(), fromTime(time, delta));
    }

    private static double fromTime(long time, float delta){
        if(time > delta) return 0;
        else if (time == 0) return 100;
        else return 1 - (time / delta);
    }

}
