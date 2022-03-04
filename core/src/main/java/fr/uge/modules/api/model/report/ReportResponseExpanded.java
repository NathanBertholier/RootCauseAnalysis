package fr.uge.modules.api.model.report;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.linking.Relation;
import fr.uge.modules.api.serializer.ExpendedReportSerializer;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;

@JsonSerialize(using = ExpendedReportSerializer.class)
public record ReportResponseExpanded(ReportResponse reportResponseBase, PriorityQueue<Relation> relations) implements GenericReport {

    @Override
    public LogEntity getRoot() {
        return reportResponseBase.getRoot();
    }

    @Override
    public LogEntity getTarget() { return reportResponseBase.getTarget(); }

    @Override
    public Set<TokensMostSeen> getSeenTokens() {
        return reportResponseBase.getSeenTokens();
    }

    @Override
    public List<LogEntity> getRelevantLogs() {
        return reportResponseBase.getRelevantLogs();
    }

    @Override
    public String toString() {
        return "ReportResponseExpanded{" +
                "reportResponseBase=" + reportResponseBase +
                ", link=" + relations +
                '}';
    }
}
