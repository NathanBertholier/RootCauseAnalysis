package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.tokenization.configuration.ConfigurationToken;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

@ApplicationScoped
public class Tokenization {
    private static final Logger LOGGER = Logger.getGlobal();

    @Named("standard")
    @Inject
    ConfigurationToken configurationToken;

    public LogEntity tokenizeLog(long id, String body) {
        Objects.requireNonNull(body);

        var words = Arrays.stream(body.split("\t")).toList();

        var log = new LogEntity();

        var timeStamp = configurationToken.getTimestamp(words);
        if(timeStamp.isEmpty()) {
            log.setDatetime(Timestamp.from(Instant.now()));
            log.setAutogeneratedDatetime(true);
        } else {
            log.setDatetime(timeStamp.get());
            log.setAutogeneratedDatetime(false);
        }

        List<TokenEntity> tokens = new ArrayList<>(configurationToken.getTokensWithIndex(words));
        tokens.addAll(configurationToken.getTokens(words));

        log.setId(id);
        log.setTokens(tokens);
        System.out.println("Tokenizer: " + log);
        return log;
    }
}
