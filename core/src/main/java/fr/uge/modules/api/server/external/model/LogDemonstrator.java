package fr.uge.modules.api.server.external.model;

import java.util.Arrays;

public record LogDemonstrator(int id, String datetime, String content, Token[] tokens) {
    @Override
    public int hashCode() {
        return Integer.hashCode(id) ^ datetime.hashCode() ^ content.hashCode() ^ Arrays.hashCode(tokens);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LogDemonstrator logDemonstrator)) return false;
        else return logDemonstrator.id == id &&
                logDemonstrator.datetime.equals(datetime) &&
                logDemonstrator.content.equals(content) &&
                Arrays.equals(logDemonstrator.tokens, tokens);
    }

    @Override
    public String toString() {
        return "LogDemonstrator{" +
                "id=" + id +
                ", datetime='" + datetime + '\'' +
                ", content='" + content + '\'' +
                ", tokens=" + Arrays.toString(tokens) +
                '}';
    }
}
