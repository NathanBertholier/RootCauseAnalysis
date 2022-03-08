package fr.uge.modules.api.model.linking;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.serializer.TokensLinkSerializer;
import fr.uge.modules.linking.strategy.EmptyStrategy;
import fr.uge.modules.linking.strategy.ProximityStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonSerialize(using = TokensLinkSerializer.class)
public class TokensLink {
    private final List<Computation> computations;
    private final double proximity;
    private final ProximityStrategy proximityFunction;

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
        return new TokensLink(Collections.emptyList(), new EmptyStrategy(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokensLink that = (TokensLink) o;
        return Double.compare(that.proximity, proximity) == 0 && computations.equals(that.computations) && proximityFunction.equals(that.proximityFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(computations, proximity, proximityFunction);
    }

    @Override
    public String toString() {
        return "LinksResponse{" +
                "computations=" + computations +
                ", link=" + proximity +
                '}';
    }
}
