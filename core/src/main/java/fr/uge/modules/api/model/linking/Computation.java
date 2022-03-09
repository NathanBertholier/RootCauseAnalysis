package fr.uge.modules.api.model.linking;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.serializer.ComputationSerializer;
import fr.uge.modules.linking.token.type.TokenType;

/**
 * Record used to represent a computation between two token values.
 * Contains the token's type, the two compared values, and a double value representing the proximity between those.
 * A computation is serialized using a ComputationSerializer object.
 */
@JsonSerialize(using = ComputationSerializer.class)
public record Computation(TokenType token_type, String value_log_first, String value_log_second, Double proximity) {
}
