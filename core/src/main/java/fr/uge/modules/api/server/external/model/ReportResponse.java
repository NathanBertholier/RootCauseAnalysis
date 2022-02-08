package fr.uge.modules.api.server.external.model;

import java.util.Arrays;

public record ReportResponse(Log log, TokenModel[] tokenModels, Log[] logs) {
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ReportResponse reportResponse)) return false;
        else return reportResponse.log.equals(log) && Arrays.equals(reportResponse.tokenModels, tokenModels) && Arrays.equals(reportResponse.logs, logs);
    }

    @Override
    public int hashCode() {
        return log.hashCode() ^ Arrays.hashCode(tokenModels) ^ Arrays.hashCode(logs);
    }

    @Override
    public String toString() {
        return "ReportResponse{" +
                "log=" + log +
                ", tokens=" + Arrays.toString(tokenModels) +
                ", logs=" + Arrays.toString(logs) +
                '}';
    }
}
