package fr.uge.modules.api.server.external.insertion;

import fr.uge.modules.api.server.external.model.Rawlog;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

@ApplicationScoped
public class RawlogProcessor {
    private final Logger LOGGER = Logger.getGlobal();

    @Incoming(value = "logTokenization")
    public CompletionStage<Void> processTokenization(Message<JsonObject> incoming){
        var log = incoming.getPayload().mapTo(Rawlog.class);
        System.out.println(log);
        Optional<IncomingRabbitMQMetadata> metadata = incoming.getMetadata(IncomingRabbitMQMetadata.class);
        var id = metadata.orElseThrow().getHeader("id", Long.class).orElseThrow();
        System.out.println(id);
        return CompletableFuture.runAsync(()->{});
    }
}

