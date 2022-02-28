package fr.uge.modules.api.model.report;

import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.entities.LogEntity;

import java.util.*;

public record ReportResponse(LogEntity root, Set<TokensMostSeen> tokens, List<LogEntity> logs) implements GenericReport {
    @Override
    public String toString() {
        return "ReportResponse{" +
                "root=" + root +
                ", tokens=" + tokens +
                ", logs=" + logs +
                '}';
    }
}
