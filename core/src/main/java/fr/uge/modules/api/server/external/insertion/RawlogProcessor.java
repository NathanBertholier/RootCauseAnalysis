package fr.uge.modules.api.server.external.insertion;

import fr.uge.db.insert.log.LogInserter;
import fr.uge.db.insert.tokens.LogTokens;
import fr.uge.modules.api.server.external.model.Rawlog;
import fr.uge.modules.api.server.external.model.TokenModel;
import fr.uge.modules.api.server.external.model.Tokens;
import fr.uge.modules.tokenization.Tokenization;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
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
    public Tokens processTokenization(Message<JsonObject> incoming){
        var log = incoming.getPayload().mapTo(Rawlog.class);
        Optional<IncomingRabbitMQMetadata> metadata = incoming.getMetadata(IncomingRabbitMQMetadata.class);
        var tokens = tokenization.tokenizeLog(log.getLog());
        System.out.println("Tokens size " + tokens.size());
        var id = metadata.orElseThrow().getHeader("id", Long.class).orElseThrow();
        return new Tokens(id, tokens.stream().map(token -> new TokenModel(token.getType().getName(), token.getValue())).toList());
    }

    @Broadcast
    @Incoming(value = "logRaw")
    public CompletionStage<Void> processRaw(Message<JsonObject> incoming) {
        var log = incoming.getPayload().mapTo(Rawlog.class);
        Optional<IncomingRabbitMQMetadata> metadata = incoming.getMetadata(IncomingRabbitMQMetadata.class);

        var id = metadata.orElseThrow().getHeader("id", Long.class).orElseThrow();
        logInserter.insert(id, log.getLog());

        LOGGER.log(Level.INFO,() -> "ID in databse : " + id + " <-> " + log.getLog());
        return CompletableFuture.runAsync(()->{});
    }

    @Incoming(value = "tokensOut")
    public void process(JsonObject incoming) {
        var tokens = incoming.mapTo(Tokens.class);
        logTokens.insertTokens(tokens.id(), null,tokens.tokens());
    }
}

