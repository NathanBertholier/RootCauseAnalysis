package fr.uge.modules.api.model;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record ReportResponse(LogEntity log, Map<Integer, TokensMostSeen> tokensReports, List<LogEntity> logs) {
    @Override
    public String toString() {
        return "ReportResponse{" +
                "log=" + log +
                ", tokensReports=" + tokensReports +
                ", logs=" + logs +
                '}';
    }
}
