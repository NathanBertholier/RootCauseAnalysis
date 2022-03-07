package fr.uge.modules.linking.strategy;

import fr.uge.modules.api.model.entities.LogEntity;
import java.math.BigDecimal;

@FunctionalInterface
public interface LinkingStrategy {
    BigDecimal computeLinks(LogEntity log1, LogEntity log2, long delta);
}
