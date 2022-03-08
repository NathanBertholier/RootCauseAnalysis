package fr.uge.modules.api.model.linking;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.serializer.ComputationSerializer;
import fr.uge.modules.linking.token.type.TokenType;

@JsonSerialize(using = ComputationSerializer.class)
public record Computation(TokenType token_type, String value_log_first, String value_log_second, Double proximity) {
}
