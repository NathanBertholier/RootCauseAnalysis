package fr.uge.db.insert.log;

import fr.uge.modules.api.model.entities.RawLog;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.util.Properties;
import java.util.logging.Logger;

@ApplicationScoped
public class LogInserter {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Properties PROPERTIES = new Properties();

    @Incoming(value = "logRaw")
    public Uni<Response> processRaw(JsonObject incoming) {
        RawLog rawLog = incoming.mapTo(RawLog.class);
        return Panache.withTransaction(rawLog::persist)
                .map(item -> Response
                        .created(URI.create("/insertlog/single/"))
                        .entity(item)
                        .build()
                );
    }
}
