package fr.uge.modules.api.model.linking;

import java.util.Collections;
import java.util.List;

public record Links(List<Computation> computations, double proximity) {
    public static Links emptyLink(int value){
        return new Links(Collections.emptyList(), value);
    }

    @Override
    public String toString() {
        return "LinksResponse{" +
                "computations=" + computations +
                ", link=" + proximity +
                '}';
    }
}
