package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.entities.RawLogEntity;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class TokenQueueProcessor {
    private final Logger LOGGER = Logger.getGlobal();
    private final Tokenization tokenization = new Tokenization();

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @Incoming(value = "token-in")
    public Uni<Void> processTokenization(JsonObject incoming){
        var rawlog = incoming.mapTo(RawLogEntity.class);
        var log = tokenization.tokenizeLog(rawlog.id,
                rawlog.log);

        return sessionFactory.withTransaction((session, transaction) -> session.persist(log)
                .onFailure()
                .invoke(() -> LOGGER.log(Level.SEVERE, "Error while inserting log id " + log.id + "in database"))
                .replaceWithVoid());

    }
}

