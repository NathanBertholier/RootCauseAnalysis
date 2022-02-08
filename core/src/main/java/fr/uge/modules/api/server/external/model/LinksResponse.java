package fr.uge.modules.api.server.external.model;

import java.util.Arrays;

public record LinksResponse(Computation[] computations, float proximity) {
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LinksResponse linksResponse)) return false;
        else return linksResponse.proximity == proximity && Arrays.equals(linksResponse.computations, computations);
    }

    @Override
    public int hashCode() {
        return Float.hashCode(proximity) ^ Arrays.hashCode(computations);
    }

    @Override
    public String toString() {
        return "LinksResponse{" +
                "computations=" + Arrays.toString(computations) +
                ", proximity=" + proximity +
                '}';
    }
}
