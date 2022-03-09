package fr.uge.modules.api.model.report;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.serializer.SimpleReportSerializer;

import java.util.*;

/**
 * Record representing a simple generated report that does not contain proximity computations.
 * This report only contains:
 * - a LogEntity rootcause, which might be the problem source,
 * - a LogEntity target, the requested log from which the report will be generated
 * - a Set of TokensMostSeen object, which list the most viewed tokens of related logs
 * - A List of LogEntity containing every related logs
 */
@JsonSerialize(using = SimpleReportSerializer.class)
public record ReportResponse(LogEntity rootCause, LogEntity target, Set<TokensMostSeen> tokens, List<LogEntity> logs) implements GenericReport {

    @Override
    public LogEntity getRoot() {
        return rootCause;
    }

    @Override
    public LogEntity getTarget() { return target; }

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
                "rootCause=" + rootCause +
                ", tokens=" + tokens +
                ", logs=" + logs +
                '}';
    }
}
