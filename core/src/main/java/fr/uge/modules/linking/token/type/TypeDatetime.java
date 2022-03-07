package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.TokensLink;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;

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

    public static Computation computeDateTimeProximity(Timestamp ldt1, Timestamp ldt2, long delta){
        var time = (ldt1.getTime() - ldt2.getTime()) / 1000;
        return new Computation(new TypeDatetime(), ldt1.toString(), ldt2.toString(), fromTime(Math.abs(time), delta));
    }

    private static double fromTime(long time, float delta){
        if(time > delta) return 0;
        else if (time == 0) return 100;
        else {
            double res = (1 - (time / delta)) * 100;
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(2);
            format.setRoundingMode(RoundingMode.FLOOR);
            return Double.parseDouble(format.format(res).replace(',','.'));
        }
    }

    @Override
    public TokensLink computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        return TokensLink.withoutStrategy(0);
    }
}
