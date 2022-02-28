package fr.uge.modules.api.model.linking;

import java.util.Collections;
import java.util.List;

public record Link(List<Computation> computations, double proximity) {
    public static Link emptyLink(int value){
        return new Link(Collections.emptyList(), value);
    }

    @Override
    public String toString() {
        return "LinksResponse{" +
                "computations=" + computations +
                ", link=" + proximity +
                '}';
    }
}
