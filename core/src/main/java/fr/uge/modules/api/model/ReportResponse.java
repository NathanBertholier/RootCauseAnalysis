package fr.uge.modules.api.model;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.*;

public record ReportResponse(LogEntity log, Set<TokensMostSeen> tokensReports, SortedMap<Double, LogEntity> relevantLogs) {
    @Override
    public String toString() {
        return "ReportResponse{" +
                "log=" + log +
                ", tokensReports=" + tokensReports +
                ", relevantLogs=" + relevantLogs +
                '}';
    }
}
