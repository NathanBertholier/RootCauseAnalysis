package fr.uge.modules.api.model.linking;

import fr.uge.modules.linking.strategy.AverageStrategy;
import fr.uge.modules.linking.strategy.ProximityStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokensLink {
    private final List<Computation> computations;
    private final double proximity;
    private final ProximityStrategy proximityFunction;
    private static final AverageStrategy averageStrategy = new AverageStrategy();

    public TokensLink(List<Computation> computations, ProximityStrategy proximityFunction){
        this.computations = computations;
        this.proximityFunction = proximityFunction;
        this.proximity = proximityFunction.computeProximity(computations);
    }

    public TokensLink addComputation(Computation computation){
        var copyComputations = new ArrayList<>(computations);
        copyComputations.add(computation);
        return new TokensLink(copyComputations, proximityFunction);
    }

    public TokensLink addComputations(List<Computation> toAddComputations){
        var copyComputations = new ArrayList<>(computations);
        copyComputations.addAll(toAddComputations);
        return new TokensLink(copyComputations, proximityFunction);
    }

    public List<Computation> getComputations(){
        return Collections.unmodifiableList(computations);
    }

    public double getProximity(){
        return proximity;
    }

    public static TokensLink withoutStrategy(double value){
        return new TokensLink(Collections.emptyList(), averageStrategy);
    }

    @Override
    public String toString() {
        return "LinksResponse{" +
                "computations=" + computations +
                ", link=" + proximity +
                '}';
    }
}
