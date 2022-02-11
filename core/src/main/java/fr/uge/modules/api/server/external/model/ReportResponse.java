package fr.uge.modules.api.server.external.model;

import java.util.Arrays;

public record ReportResponse(Log log, TokensReport[] tokensReports, Log[] logs) {
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ReportResponse reportResponse)) return false;
        else return reportResponse.log.equals(log) && Arrays.equals(reportResponse.tokensReports, tokensReports) && Arrays.equals(reportResponse.logs, logs);
    }

    @Override
    public int hashCode() {
        return log.hashCode() ^ Arrays.hashCode(tokensReports) ^ Arrays.hashCode(logs);
    }

    @Override
    public String toString() {
        return "ReportResponse{" +
                "log=" + log +
                ", tokens=" + Arrays.toString(tokensReports) +
                ", logs=" + Arrays.toString(logs) +
                '}';
    }
}
