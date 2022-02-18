package fr.uge.modules.api.endpoint.insertion;

import fr.uge.modules.api.model.entities.RawLogEntity;
import fr.uge.modules.tokenization.Tokenization;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.logging.Logger;

@ApplicationScoped
public class RawlogProcessor {
    private final Logger LOGGER = Logger.getGlobal();
    private final Tokenization tokenization = new Tokenization();

    @Incoming(value = "logTokenization")
    public Uni<Response> processTokenization(JsonObject incoming){
        System.out.println("Incoming : " + incoming);
        var rawlog = incoming.mapTo(RawLogEntity.class);
        var log = tokenization.tokenizeLog(rawlog.id,
                rawlog.log);

        return Panache.withTransaction(log::persist)
                .map(item -> Response
                        .created(URI.create("/insertlog"))
                        .entity(item)
                        .build()
                );
    }
}

