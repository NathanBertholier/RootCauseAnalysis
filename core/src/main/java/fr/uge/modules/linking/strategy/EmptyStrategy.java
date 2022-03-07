package fr.uge.modules.linking.strategy;

import fr.uge.modules.api.model.linking.Computation;

import java.util.List;

public class EmptyStrategy implements ProximityStrategy {
    private final double value;

    public EmptyStrategy(double value){
        this.value = value;
    }
    @Override
    public double computeProximity(List<Computation> computations) {
        return value;
    }
}
