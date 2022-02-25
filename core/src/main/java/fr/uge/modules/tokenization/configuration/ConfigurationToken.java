package fr.uge.modules.tokenization.configuration;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.linking.token.type.TokenType;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ConfigurationToken {
    List<TokenType> getTokenType();

    List<TokenEntity> tokenization(List<String> words);

    List<TokenEntity> parseTokens(List<String> words);

    Optional<Timestamp> getTimestamp(List<String> words);
}
