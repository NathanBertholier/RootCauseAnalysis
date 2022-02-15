package fr.uge.db.insert.log;

import fr.uge.modules.api.model.entities.RawLog;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import javax.enterprise.context.ApplicationScoped;

import java.util.Properties;
import java.util.logging.Logger;

@ApplicationScoped
public class LogInserter {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Properties PROPERTIES = new Properties();



    /*@Incoming(value = "logRaw")
    public Uni<Void> processRaw(JsonObject incoming) {
        RawLog rawLog = incoming.mapTo(RawLog.class);
        return Panache.withTransaction(rawLog::persist)
                .map(item -> Response
                        .created(URI.create("/insertlog/single/"))
                        .entity(item)
                        .build()
                );
        return Uni.createFrom().voidItem();
    }*/
}
