package fr.uge.db.insert.log;

import fr.uge.modules.api.model.entities.RawLog;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Table;
import javax.transaction.Transactional;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

@ApplicationScoped
public class LogInserter {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Properties PROPERTIES = new Properties();

    public void insert(RawLog rawLog) {
        //RawLog.persist(rawLog);
    }

    @Transactional
    @Incoming(value = "logRaw")
    public CompletionStage<Void> processRaw(JsonObject incoming) {
        RawLog rawLog = incoming.mapTo(RawLog.class);
        System.out.println("Inserting: " +rawLog + " ID :" + rawLog.getId());
        rawLog.persistAndFlush();
        return CompletableFuture.runAsync(()->{});
    }
}
