package fr.uge.modules.api.server.external.model;

import java.util.Arrays;

public record LogDemonstrator(long id, String datetime, String content, TokenModel[] tokenModels) {
    @Override
    public int hashCode() {
        return Long.hashCode(id) ^ datetime.hashCode() ^ content.hashCode() ^ Arrays.hashCode(tokenModels);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LogDemonstrator logDemonstrator)) return false;
        else return logDemonstrator.id == id &&
                logDemonstrator.datetime.equals(datetime) &&
                logDemonstrator.content.equals(content) &&
                Arrays.equals(logDemonstrator.tokenModels, tokenModels);
    }

    @Override
    public String toString() {
        return "LogDemonstrator{" +
                "id=" + id +
                ", datetime='" + datetime + '\'' +
                ", content='" + content + '\'' +
                ", tokens=" + Arrays.toString(tokenModels) +
                '}';
    }
}
