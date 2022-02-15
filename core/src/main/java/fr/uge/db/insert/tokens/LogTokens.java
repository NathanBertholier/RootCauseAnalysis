package fr.uge.db.insert.tokens;

import fr.uge.modules.api.model.entities.Log;
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
public class LogTokens {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Properties PROPERTIES = new Properties();

    @Incoming(value = "tokensOut")
    public Uni<Response> process(JsonObject incoming) {
        var log = incoming.mapTo(Log.class);
        System.out.println(log);
        log.getTokens().forEach(System.out::println);
        return Panache.withTransaction(log::persist)
                .map(item -> Response
                        .created(URI.create("/insertlog/single/"))
                        .entity(item)
                        .build()
                );
    }
}
