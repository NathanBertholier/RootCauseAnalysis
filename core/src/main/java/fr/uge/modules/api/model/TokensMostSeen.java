package fr.uge.modules.api.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.serializer.TokenMostSeenSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@JsonSerialize(using = TokenMostSeenSerializer.class)
public record TokensMostSeen(String token_type, List<String> token_values, long count) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokensMostSeen that = (TokensMostSeen) o;
        return count == that.count && token_type.equals(that.token_type) && token_values.equals(that.token_values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token_type, token_values, count);
    }

    @Override
    public String toString() {
        return "TokensMostSeen{" +
                "token_type='" + token_type + '\'' +
                ", token_values=" + token_values +
                ", count=" + count +
                '}';
    }
}
