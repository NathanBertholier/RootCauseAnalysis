package fr.uge.modules.linking.strategy;

import fr.uge.modules.api.model.linking.Computation;

import java.util.List;

@FunctionalInterface
public interface ProximityStrategy {
    double computeProximity(List<Computation> computations);
}
