package fr.uge.modules.linking.strategy;

import fr.uge.modules.api.model.CompleteLog;
import io.smallrye.mutiny.Uni;

import java.math.BigDecimal;
import java.util.function.BiFunction;

@FunctionalInterface
public interface LinkingStrategy {
    Uni<BigDecimal> computeLinks(Uni<CompleteLog> log1, Uni<CompleteLog> log2);
}
