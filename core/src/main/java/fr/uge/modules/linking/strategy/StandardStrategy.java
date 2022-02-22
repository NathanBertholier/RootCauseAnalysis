package fr.uge.modules.linking.strategy;

import fr.uge.modules.api.model.CompleteLog;
import io.smallrye.mutiny.Uni;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

public class StandardStrategy implements LinkingStrategy {
    @Override
    public Uni<BigDecimal> computeLinks(Uni<CompleteLog> log1, Uni<CompleteLog> log2) {
        return Uni.createFrom().item(BigDecimal.ZERO);
    }
}
