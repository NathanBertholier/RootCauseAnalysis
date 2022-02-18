package fr.uge.modules.api.model;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.Arrays;
import java.util.Set;

public record ReportResponse(CompleteLog log, Set<TokensMostSeen> tokensReports, CompleteLog[] logs) {
    @Override
    public String toString() {
        return "ReportResponse{" +
                "log=" + log +
                ", tokensReports=" + tokensReports +
                ", logs=" + Arrays.toString(logs) +
                '}';
    }
}
