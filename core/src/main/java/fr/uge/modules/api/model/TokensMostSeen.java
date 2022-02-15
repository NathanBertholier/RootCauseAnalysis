package fr.uge.modules.api.model;

import java.util.Arrays;

public record TokensMostSeen(String token_type, String[] token_value, long count) {
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof TokensMostSeen tokensReport)) return false;
        else return tokensReport.count == count &&
                tokensReport.token_type.equals(token_type) &&
                Arrays.equals(tokensReport.token_value, token_value);
    }

    @Override
    public int hashCode() {
        return token_type.hashCode() ^ Arrays.hashCode(token_value) ^ Long.hashCode(count);
    }

    @Override
    public String toString() {
        return "TokensReport{" +
                "token_type='" + token_type + '\'' +
                ", token_value=" + Arrays.toString(token_value) +
                ", count=" + count +
                '}';
    }
}
