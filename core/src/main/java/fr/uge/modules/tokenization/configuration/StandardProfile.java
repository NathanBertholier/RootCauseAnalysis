package fr.uge.modules.tokenization.configuration;

import fr.uge.modules.linking.token.type.*;
import fr.uge.modules.tokenization.configuration.timestamp.DateTimeFormatter;
import fr.uge.modules.tokenization.configuration.timestamp.DefaultDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.sql.Timestamp;
import java.util.*;

@Named("standard")
@ApplicationScoped
public class StandardProfile implements Profile {
    private final HashMap<TokenType, Integer> tokenTypeIndex = new HashMap<>();
    private final ArrayList<TokenType> tokenTypes = new ArrayList<>();
    private final DateTimeFormatter dateTimeFormatter = new DefaultDateTime();


    public StandardProfile() {
        tokenTypeIndex.put(new TypeIPv4(), -1);
        tokenTypeIndex.put(new TypeHTTPStatus(), -1);
        tokenTypeIndex.put(new TypeEdgeResponse(), -1);
        tokenTypeIndex.put(new TypeIPv6(), -1);
        tokenTypeIndex.put(new TypeURL(), -1);
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

