package fr.uge.modules.api.model;

import java.util.Arrays;

public record TokensResponse(CompleteLog[] logDemonstrators) {
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof TokensResponse tokensResponse)) return false;
        else return Arrays.equals(tokensResponse.logDemonstrators, logDemonstrators);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(logDemonstrators);
    }

    @Override
    public String toString() {
        return "TokensResponse{" +
                "dd=" + Arrays.toString(logDemonstrators) +
                '}';
    }
}
