package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.RawLogEntity;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Merge;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.hibernate.reactive.mutiny.Mutiny;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logmanager.Level;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class TokenQueueProcessor {
    private final Logger LOGGER = Logger.getGlobal();
    private final Tokenization tokenization = new Tokenization();

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @Incoming(value = "token-in")
    @Outgoing(value = "batch-processor")
    public Multi<List<LogEntity>> processTokenization(Multi<JsonObject> incoming){
        return incoming.map(rawLog -> rawLog.mapTo(RawLogEntity.class))
                .map(rawLogEntity -> tokenization.tokenizeLog(rawLogEntity.id, rawLogEntity.log))
                .group()
                .intoLists()
                .every(Duration.ofMillis(3000));
    }

    @Incoming(value = "batch-processor")
    @Merge
    public Uni<Void> processBatch(List<LogEntity> logs) {
        return Panache.withTransaction(() -> LogEntity.persist(logs))
                .onFailure()
                .invoke(() -> LOGGER.log(Level.SEVERE, "Error while inserting log id in database"))
                .replaceWithVoid();
    }
}
