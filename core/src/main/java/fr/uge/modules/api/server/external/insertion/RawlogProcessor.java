package fr.uge.modules.api.server.external.insertion;

import fr.uge.db.insert.logtoken.LogInserter;
import fr.uge.modules.api.server.external.model.Rawlog;
import fr.uge.modules.api.server.external.model.TokenModel;
import fr.uge.modules.api.server.external.model.Tokens;
import fr.uge.modules.tokenization.Tokenization;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.SQLException;
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
    LogInserter inserter;

    @Incoming(value = "logTokenization")
    @Outgoing(value = "tokens")
    public Tokens processTokenization(Message<JsonObject> incoming){
        var log = incoming.getPayload().mapTo(Rawlog.class);
        Optional<IncomingRabbitMQMetadata> metadata = incoming.getMetadata(IncomingRabbitMQMetadata.class);
        var tokens = tokenization.tokenizeLog(log.getLog());
        System.out.println("Tokens size " + tokens.size());
        var id = metadata.orElseThrow().getHeader("id", Long.class).orElseThrow();
        //System.out.println("Log : " + log + " ID : " + id);
        return new Tokens(id, tokens.stream().map(token -> new TokenModel(token.getType().getName(), token.getValue())).toList());
    }

    @Incoming(value = "logRaw")
    public CompletionStage<Void> processRaw(Message<JsonObject> incoming) {
        var log = incoming.getPayload().mapTo(Rawlog.class).getLog();
        Optional<IncomingRabbitMQMetadata> metadata = incoming.getMetadata(IncomingRabbitMQMetadata.class);
        var id = metadata.orElseThrow().getHeader("id", Long.class).orElseThrow();
        try {
            inserter.insertInMonitoring(id, log);
            LOGGER.log(Level.INFO,() -> "ID in databse : " + id + " <-> " + log);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"SQLException",e);
        }
        return CompletableFuture.runAsync(()->{});
    }


    @Incoming(value = "tokensOut")
    public void process(JsonObject incoming) {
        var tokens = incoming.mapTo(Tokens.class);
        System.out.println("TOKENS :");
        tokens.tokens().forEach(System.out::println);
    }
}

