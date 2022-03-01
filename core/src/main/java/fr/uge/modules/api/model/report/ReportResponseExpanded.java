package fr.uge.modules.api.model.report;

import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.linking.LinkResponse;

import java.util.List;
import java.util.Set;

public record ReportResponseExpanded(ReportResponse reportResponseBase, Set<LinkResponse> proximity) implements GenericReport {
    @Override
    public LogEntity getRoot() {
        return reportResponseBase.getRoot();
    }

    @Override
    public Set<TokensMostSeen> getSeenTokens() {
        return reportResponseBase.getSeenTokens();
    }

    @Override
    public Set<LogEntity> getRelevantLogs() {
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
