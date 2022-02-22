package fr.uge.modules.api.model.linking;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

public record LinksResponse(Computation[] computations, BigDecimal proximity) {
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LinksResponse linksResponse)) return false;
        else return Objects.equals(linksResponse.proximity, proximity) && Arrays.equals(linksResponse.computations, computations);
    }

    @Override
    public int hashCode() {
        return proximity.hashCode() ^ Arrays.hashCode(computations);
    }

    @Override
    public String toString() {
        return "LinksResponse{" +
                "computations=" + Arrays.toString(computations) +
                ", proximity=" + proximity +
                '}';
    }
}
