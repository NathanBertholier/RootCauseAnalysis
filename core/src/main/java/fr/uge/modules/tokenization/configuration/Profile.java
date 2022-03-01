package fr.uge.modules.tokenization.configuration;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.linking.token.type.TokenType;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface Profile {
    List<TokenType> getTokenType();

    HashMap<TokenType, Integer> getTokenTypeIndex();

    Optional<Timestamp> getTimestamp(List<String> words);
}
