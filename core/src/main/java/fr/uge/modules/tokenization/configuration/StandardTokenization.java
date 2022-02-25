package fr.uge.modules.tokenization.configuration;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.linking.token.type.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Named("standard")
@ApplicationScoped
public class StandardTokenization implements ConfigurationToken {
    private final HashMap<TokenType, Integer> tokenTypeIndex = new HashMap<>();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final int dateIndex = 0;
    private final int timeIndex = 1;

    public StandardTokenization() {
        tokenTypeIndex.put(new TypeIPv4(), -1);
        tokenTypeIndex.put(new TypeHTTPStatus(), -1);
        tokenTypeIndex.put(new TypeEdgeResponse(), -1);
        tokenTypeIndex.put(new TypeIPv6(), -1);
    }

    @Override
    public List<TokenType> getTokenType() {
        return List.copyOf(tokenTypeIndex.keySet());
    }

    @Override
    public List<TokenEntity> tokenization(List<String> words) {
        return tokenTypeIndex.entrySet()
                .stream()
                .filter(k -> k.getValue() != -1)
                .map(k -> {
                    TokenEntity token = new TokenEntity();
                    token.setIdtokentype(k.getKey().getTokenTypeId());
                    token.setValue(words.get(k.getValue()));
                    return token;
                }).toList();
    }

    @Override
    public List<TokenEntity> parseTokens(List<String> words) {
        return tokenTypeIndex.entrySet()
                .stream()
                .filter(k -> k.getValue() == -1)
                .map(Map.Entry::getKey)
                .map(tokenType -> words.stream()
                        .filter(word -> tokenType.matcher(word) != -1)
                        .map(word -> {
                            TokenEntity token = new TokenEntity();
                            token.setIdtokentype(tokenType.getTokenTypeId());
                            token.setValue(word);
                            return token;
                        }).toList()
                ).flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Timestamp> getTimestamp(List<String> words) {
        try {
            return Optional.of(new Timestamp(this.formatter.parse(words.get(dateIndex) + " " + words.get(timeIndex))
                    .getTime()));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }
}

