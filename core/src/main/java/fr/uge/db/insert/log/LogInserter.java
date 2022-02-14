package fr.uge.db.insert.log;

import fr.uge.modules.api.model.RawLog;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class LogInserter {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Properties PROPERTIES = new Properties();

    public void insert(RawLog rawLog) {
        //RawLog.persist(rawLog);
    }

    @Incoming(value = "logRaw")
    public CompletionStage<Void> processRaw(Message<JsonObject> incoming) {
        var log = incoming.getPayload().mapTo(RawLog.class);
        Optional<IncomingRabbitMQMetadata> metadata = incoming.getMetadata(IncomingRabbitMQMetadata.class);

        var id = metadata.orElseThrow().getHeader("id", Long.class).orElseThrow();
        System.out.println("Inserting: " + log);
        this.insert(log);

        return CompletableFuture.runAsync(()->{});
    }
}
