package fr.uge.modules.api.model.report;

import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.linking.Relation;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public record ReportResponseExpanded(ReportResponse reportResponseBase, SortedSet<Relation> proximity) implements GenericReport {
    @Override
    public LogEntity getRoot() {
        return reportResponseBase.getRoot();
    }

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
                ", link=" + proximity +
                '}';
    }
}
