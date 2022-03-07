package fr.uge.modules.tokenization.configuration;

import fr.uge.modules.linking.token.type.TokenType;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * A profile is used to tokenize logs in multiples ways.
 */
public interface Profile {
    List<TokenType> getTokenType();

    HashMap<TokenType, Integer> getTokenTypeIndex();

    Optional<Timestamp> getTimestamp(List<String> words);
}
