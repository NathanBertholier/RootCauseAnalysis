package fr.uge.modules.api.server.external.model;

import java.util.Arrays;

public record ReportResponseExpanded(ReportResponse reportResponseBase, Proximity[] proximity) {
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ReportResponseExpanded reportResponseExpanded)) return false;
        else return reportResponseExpanded.reportResponseBase.equals(reportResponseBase) && Arrays.equals(reportResponseExpanded.proximity, proximity);
    }

    @Override
    public int hashCode() {
        return reportResponseBase.hashCode() ^ Arrays.hashCode(proximity);
    }

    @Override
    public String toString() {
        return "ReportResponseExpanded{" +
                "reportResponseBase=" + reportResponseBase +
                ", proximity=" + Arrays.toString(proximity) +
                '}';
    }
}
