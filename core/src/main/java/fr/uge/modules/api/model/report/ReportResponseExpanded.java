package fr.uge.modules.api.model.report;

import fr.uge.modules.api.model.linking.LinkResponse;

import java.util.Set;

public record ReportResponseExpanded(ReportResponse reportResponseBase, Set<LinkResponse> proximity) implements GenericReport {
    @Override
    public String toString() {
        return "ReportResponseExpanded{" +
                "reportResponseBase=" + reportResponseBase +
                ", link=" + proximity +
                '}';
    }
}
