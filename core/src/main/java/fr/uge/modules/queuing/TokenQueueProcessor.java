package fr.uge.modules.queuing;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.RawLogEntity;
import fr.uge.modules.tokenization.Tokenization;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TokenQueueProcessor {

    @Inject
    Tokenization tokenization;

    /**
     * Get message from queue Token in exchanges 'token-in'
     * @param incoming  Message in JSON contained in the queue
     * @return          A LogEntity inserted in a new exchange batch-processor linked in the queue.
     */
    @Incoming(value = "token-in")
    @Outgoing(value = "batch-processor")
    public LogEntity processTokenization(JsonObject incoming) {
        var rawLogEntity = incoming.mapTo(RawLogEntity.class);
        return tokenization.tokenizeLog(rawLogEntity.id, rawLogEntity.log);
    }
}

