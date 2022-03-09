package fr.uge.modules.api.model.linking;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.serializer.TokensLinkSerializer;
import fr.uge.modules.linking.strategy.EmptyStrategy;
import fr.uge.modules.linking.strategy.ProximityStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Class used to represent a calculation object between two logs.
 * It contains every computation used to determine the log proximity, according to a ProximityStrategy function.
 * ProximityStrategy is a List -> Double function that determines how is the proximity calculated according to
 * a computations list.
 * A TokensLink is serialized using a TokensLinkSerializer object.
 */
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

    /**
     * Functionnal method used to add a computation to an existing TokensLink.
     * It returns a new TokensLink containing the old computations list with the new one.
     * @param computation the computation object to add to the existing TokensLink
     * @return a new TokensLink containing the old computations list with the new one.
     */
    public TokensLink addComputation(Computation computation){
        Objects.requireNonNull(computation);
        var copyComputations = new ArrayList<>(computations);
        copyComputations.add(computation);
        return new TokensLink(copyComputations, proximityFunction);
    }

    /**
     * @return an unmodifiable list containing every computation.
     */
    public List<Computation> getComputations(){
        return Collections.unmodifiableList(computations);
    }

    /**
     * @return a double value representing the proximity between two logs
     */
    public double getProximity(){
        return proximity;
    }

    /**
     * Static factory method used to create an empty-computation TokensLink with a specific proximity value.
     * This kind of object can be handy when dealing with constant link between two tokens that does not contain
     * any computation.
     * The returned TokensLink object uses an EmptyStrategy object, with a proximity computation function that is
     * __ -> value, meaning that even if a computation is added to the TokensLink the proximity value will not
     * be modified.
     * @param value the specific proximity value
     * @return an empty-computation TokensLink with a constant proximity value.
     */
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
