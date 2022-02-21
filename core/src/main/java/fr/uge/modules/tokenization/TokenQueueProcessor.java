package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.RawLogEntity;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.rabbitmq.i18n.RabbitMQLogging;
import io.vertx.core.json.JsonArray;
import org.hibernate.reactive.mutiny.Mutiny;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logmanager.Level;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

@ApplicationScoped
public class TokenQueueProcessor {
    private final Logger LOGGER = Logger.getGlobal();
    private final Tokenization tokenization = new Tokenization();
    private final ArrayList<LogEntity> logs = new ArrayList<>();

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @Incoming(value = "token-in")
    public Uni<Void> processTokenization(JsonArray incoming){
        var list = IntStream.range(0, incoming.size())
                .mapToObj(i -> {
                    var rawLog = incoming.getJsonObject(i).mapTo(RawLogEntity.class);
                    return tokenization.tokenizeLog(rawLog.id, rawLog.log);
                }).toList();
        return Panache.withTransaction(() -> LogEntity.persist(list)
                .onFailure()
                .invoke(() -> LOGGER.log(Level.SEVERE, "Error while inserting log id in database")));

        /*var rawlog = incoming.mapTo(RawLogEntity.class);
        /var log = tokenization.tokenizeLog(rawlog.id,
                rawlog.log);

        return sessionFactory.withTransaction((session, transaction) -> session.persist(log)
                .onFailure()
                .invoke(() -> LOGGER.log(Level.SEVERE, "Error while inserting log id " + log.id + "in database"))
                .replaceWithVoid());*/
    }
}
