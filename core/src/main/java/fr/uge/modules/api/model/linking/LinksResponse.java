package fr.uge.modules.api.model.linking;

import java.math.BigDecimal;
import java.util.List;

public record LinksResponse(List<Computation> computations, BigDecimal proximity) {
    @Override
    public String toString() {
        return "LinksResponse{" +
                "computations=" + computations +
                ", proximity=" + proximity +
                '}';
    }
}
