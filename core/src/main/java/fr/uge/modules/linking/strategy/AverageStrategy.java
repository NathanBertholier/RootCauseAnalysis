package fr.uge.modules.linking.strategy;

import fr.uge.modules.api.model.linking.Computation;

import java.util.List;

public class AverageStrategy implements ProximityStrategy {
    @Override
    public double computeProximity(List<Computation> computations) {
        return computations.stream().mapToDouble(Computation::proximity).sum() / computations.size();
    }
}
