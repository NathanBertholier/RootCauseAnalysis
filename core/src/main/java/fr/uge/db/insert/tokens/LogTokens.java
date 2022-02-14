package fr.uge.db.insert.tokens;

import fr.uge.modules.api.model.TokenModel;
import fr.uge.modules.api.model.Tokens;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

@ApplicationScoped
public class LogTokens {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Properties PROPERTIES = new Properties();


    @Incoming(value = "tokensOut")
    public void process(JsonObject incoming) {
        var tokens = incoming.mapTo(Tokens.class);
        tokens.persist();
    }
}
