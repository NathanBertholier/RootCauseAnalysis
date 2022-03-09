package fr.uge.modules.linking.strategy;

import fr.uge.modules.api.model.linking.Computation;

import java.util.List;

/**
 * Strategy used to compute a proximity between two logs according to their computations list.
 * This interface is a Functional one, meaning that lambda strategies can be created at any time
 */
@FunctionalInterface
public interface ProximityStrategy {
    double computeProximity(List<Computation> computations);
}
