package fr.uge.modules.tokenization.configuration;

import fr.uge.modules.linking.token.type.*;
import fr.uge.modules.tokenization.configuration.timestamp.CommonDateTime;
import fr.uge.modules.tokenization.configuration.timestamp.DateTimeFormatter;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Timestamp;
import java.util.*;

/**
 * The Standard Profile will iterate over each words to found token.
 */
@ApplicationScoped
public class StandardProfile implements Profile {
    private final HashMap<TokenType, Integer> tokenTypeIndex = new HashMap<>();
    private final ArrayList<TokenType> tokenTypes = new ArrayList<>();
    private final DateTimeFormatter dateTimeFormatter = new CommonDateTime();

    public StandardProfile() {
        tokenTypes.add(new TypeIPv4());
        tokenTypes.add(new TypeResource());
        tokenTypes.add(new TypeHTTPStatus());
        tokenTypes.add(new TypeURL());
        tokenTypes.add(new TypeEdgeResponse());
        tokenTypes.add(new TypeIPv6());
    }

    @Override
    public List<TokenType> getTokenType() {
        return this.tokenTypes;
    }

    @Override
    public HashMap<TokenType, Integer> getTokenTypeIndex() {
        return this.tokenTypeIndex;
    }

    @Override
    public Optional<Timestamp> getTimestamp(List<String> words) {
        return dateTimeFormatter.getTimestamp(words);
    }
}

