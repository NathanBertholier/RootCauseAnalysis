package fr.uge.modules.api.endpoint.insertion;

import fr.uge.modules.api.model.TokenModel;
import fr.uge.modules.api.model.Tokens;
import fr.uge.modules.api.model.RawLog;
import fr.uge.modules.tokenization.Tokenization;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class RawlogProcessor {
    private final Logger LOGGER = Logger.getGlobal();

    @Inject
    Tokenization tokenization;

    @Incoming(value = "logTokenization")
    @Outgoing(value = "tokens")
    public Tokens processTokenization(Message<JsonObject> incoming){
        var log = incoming.getPayload().mapTo(RawLog.class);
        Optional<IncomingRabbitMQMetadata> metadata = incoming.getMetadata(IncomingRabbitMQMetadata.class);
        var tokens = tokenization.tokenizeLog(log.getLog());
        var id = metadata.orElseThrow().getHeader("id", Long.class).orElseThrow();
        return new Tokens(id, tokens.stream().map(token -> new TokenModel(token.getType().getName(), token.getValue())).toList());
    }
}

