package fr.uge.modules.tokenization.configuration;

import fr.uge.modules.linking.token.type.*;
import fr.uge.modules.tokenization.configuration.timestamp.DateTimeFormatter;
import fr.uge.modules.tokenization.configuration.timestamp.DefaultDateTime;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Timestamp;
import java.util.*;

@ApplicationScoped
public class AWSProfile implements Profile {
    private final HashMap<TokenType, Integer> tokenTypeIndex = new HashMap<>();
    private final ArrayList<TokenType> tokenTypes = new ArrayList<>();
    private final DateTimeFormatter dateTimeFormatter = new DefaultDateTime();

    public AWSProfile() {
        tokenTypeIndex.put(new TypeIPv4(), 4);
        tokenTypeIndex.put(new TypeResource(), 7);
        tokenTypeIndex.put(new TypeHTTPStatus(), 8);
        tokenTypeIndex.put(new TypeURL(), 9);
        tokenTypeIndex.put(new TypeEdgeResponse(), 13);
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
