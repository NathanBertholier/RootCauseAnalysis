package fr.uge.modules.linking.strategy;

import fr.uge.modules.api.model.CompleteLog;
import fr.uge.modules.api.model.entities.LogEntity;
import io.smallrye.mutiny.Uni;

import java.math.BigDecimal;
import java.util.function.BiFunction;

@FunctionalInterface
public interface LinkingStrategy {
    BigDecimal computeLinks(LogEntity log1, LogEntity log2, long delta);
}
