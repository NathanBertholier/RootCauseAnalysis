package fr.uge.modules.api.endpoint.insertion;

import fr.uge.modules.api.model.entities.Log;
import fr.uge.modules.api.model.entities.RawLog;
import fr.uge.modules.tokenization.Tokenization;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;

@ApplicationScoped
public class RawlogProcessor {
    private final Logger LOGGER = Logger.getGlobal();

    @Inject
    Tokenization tokenization;

    @Incoming(value = "logTokenization")
    @Outgoing(value = "tokens")
    public Log processTokenization(JsonObject incoming){
        var log = incoming.mapTo(RawLog.class);
        System.out.println(log);
        //log.persistAndFlush();
        return tokenization.tokenizeLog(log.getId(),
                log.getValue());
    }
}

