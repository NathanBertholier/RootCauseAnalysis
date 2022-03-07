package fr.uge.modules.linking.strategy;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.linking.token.type.TypeDatetime;

import java.math.BigDecimal;

public class StandardStrategy implements LinkingStrategy {
    @Override
    public BigDecimal computeLinks(LogEntity log1, LogEntity log2, long delta) {
        var timeResult = TypeDatetime.computeDateTimeProximity(log1.datetime, log2.datetime, delta);
        return BigDecimal.ZERO;
    }
}
