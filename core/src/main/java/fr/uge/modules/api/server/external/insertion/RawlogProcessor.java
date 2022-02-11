package fr.uge.modules.api.server.external.insertion;

import fr.uge.db.insert.log.LogInserter;
import fr.uge.db.insert.tokens.LogTokens;
import fr.uge.modules.api.server.external.model.Rawlog;
import fr.uge.modules.api.server.external.model.TokenModel;
import fr.uge.modules.api.server.external.model.Tokens;
import fr.uge.modules.tokenization.Tokenization;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

@ApplicationScoped
public class RawlogProcessor {
    private final Logger LOGGER = Logger.getGlobal();

    @Inject
    Tokenization tokenization;

    @Inject
    LogInserter logInserter;

    @Inject
    LogTokens logTokens;

    @Incoming(value = "logTokenization")
    @Outgoing(value = "tokens")
    @Transactional
    @Blocking
    public Tokens processTokenization(Message<JsonObject> incoming){
        var log = incoming.getPayload().mapTo(Rawlog.class);
        Optional<IncomingRabbitMQMetadata> metadata = incoming.getMetadata(IncomingRabbitMQMetadata.class);
        var tokens = tokenization.tokenizeLog(log.getLog());
        var id = metadata.orElseThrow().getHeader("id", Long.class).orElseThrow();
        return new Tokens(id, tokens.stream().map(token -> new TokenModel(token.getType(), token.getValue())).toList());
    }

    @Incoming(value = "logRaw")
    @Transactional
    @Blocking
    public CompletionStage<Void> processRaw(Message<JsonObject> incoming) {
        var log = incoming.getPayload().mapTo(Rawlog.class);
        Optional<IncomingRabbitMQMetadata> metadata = incoming.getMetadata(IncomingRabbitMQMetadata.class);

        var id = metadata.orElseThrow().getHeader("id", Long.class).orElseThrow();
        logInserter.insert(id, log.getLog());

        return CompletableFuture.runAsync(()->{});
    }

    @Incoming(value = "tokensOut")
    @Transactional
    @Blocking
    public void process(JsonObject incoming) {
        var tokens = incoming.mapTo(Tokens.class);
        logTokens.insertTokens(tokens.id(), null,tokens.tokens());
    }
}

