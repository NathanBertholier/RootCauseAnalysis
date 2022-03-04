package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.RawLogEntity;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TokenQueueProcessor {
    @Inject
    Tokenization tokenization;

    @Incoming(value = "token-in")
    @Outgoing(value = "batch-processor")
    public LogEntity processTokenization(JsonObject incoming) {
        var rawLogEntity = incoming.mapTo(RawLogEntity.class);
        return tokenization.tokenizeLog(rawLogEntity.id, rawLogEntity.log);
    }
}

