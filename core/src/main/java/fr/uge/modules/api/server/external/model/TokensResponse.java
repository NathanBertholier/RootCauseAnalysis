package fr.uge.modules.api.server.external.model;

import java.util.Arrays;

public record TokensResponse(LogDemonstrator[] logDemonstrators) {
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
                "logDemonstrators=" + Arrays.toString(logDemonstrators) +
                '}';
    }
}
