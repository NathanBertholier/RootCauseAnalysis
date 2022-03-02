package fr.uge.modules.api.model.report;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.serializer.SimpleReportSerializer;

import java.util.*;

@JsonSerialize(using = SimpleReportSerializer.class)
public record ReportResponse(LogEntity root, Set<TokensMostSeen> tokens, List<LogEntity> logs) implements GenericReport {
    @Override
    public LogEntity getRoot() {
        return root;
    }

    @Override
    public Set<TokensMostSeen> getSeenTokens() {
        return tokens;
    }

    @Override
    public List<LogEntity> getRelevantLogs() {
        return logs;
    }

    @Override
    public String toString() {
        return "ReportResponse{" +
                "root=" + root +
                ", tokens=" + tokens +
                ", logs=" + logs +
                '}';
    }
}
