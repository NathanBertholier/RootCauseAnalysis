package fr.uge.modules.api.server.external.model;

import fr.uge.modules.data.token.type.TokenType;

public record TokenModel(TokenType token_type, String token_value) {
}
