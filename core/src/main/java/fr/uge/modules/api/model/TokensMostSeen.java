package fr.uge.modules.api.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.serializer.TokenMostSeenSerializer;

import java.util.List;
import java.util.Objects;

/**
 * Record representing the most viewed tokens according to fetched logs
 * A TokensMostSeen object contains the following attributes:
 * - token_type, a string representing the specific type of the tokens
 * - token_values, a list containing each token value
 * - count, the number of those tokens values
 */
@JsonSerialize(using = TokenMostSeenSerializer.class)
public record TokensMostSeen(String token_type, List<String> token_values, long count) {
    @Override
    public String toString() {
        return "TokensMostSeen{" +
                "token_type='" + token_type + '\'' +
                ", token_values=" + token_values +
                ", count=" + count +
                '}';
    }
}
