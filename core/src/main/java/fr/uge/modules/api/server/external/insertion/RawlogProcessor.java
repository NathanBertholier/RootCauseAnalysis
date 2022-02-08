package fr.uge.modules.api.server.external.insertion;

import fr.uge.modules.api.server.external.model.Log;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.reactive.messaging.annotations.Merge;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class RawlogProcessor {
    private final Logger LOGGER = Logger.getGlobal();

    @Incoming(value = "logTokenization")
    public void processTokenization(JsonObject input){
        Log log = input.mapTo(Log.class);
        System.out.println(log);
        LOGGER.log(Level.INFO,() -> "TOKEN : Processing log ");
    }

    @Incoming(value = "logRaw")
    public void processRaw(JsonObject input) {
        Log log = input.mapTo(Log.class);

        LOGGER.log(Level.INFO, () -> "RAW : Processing log " + log);
    }

    @Incoming(value = "logs")
    //@Outgoing(value = "logs-ids")
    @NonBlocking
    public void process(Rawlog input){
        System.out.println("Processing log " + input.toString());
    }
}

