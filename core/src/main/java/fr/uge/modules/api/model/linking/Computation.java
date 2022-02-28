package fr.uge.modules.api.model.linking;

import fr.uge.modules.linking.token.type.TokenType;

public record Computation(TokenType token_type, String value_log_first, String value_log_second, Double proximity) {
}
