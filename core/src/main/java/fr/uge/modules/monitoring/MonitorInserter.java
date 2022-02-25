package fr.uge.modules.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import io.quarkus.hibernate.reactive.panache.Panache;
import fr.uge.modules.api.model.entities.MonitoringEntity;
import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class MonitorInserter {
    private static final String QUEUE_NAME = "token";
    private final Logger logger = Logger.getGlobal();

    @ConfigProperty(name = "rabbitmq-host")
    String rabbitmqurl;
    @ConfigProperty(name = "rabbitmqmonitor-port")
    String rabbitmqport;


    @Scheduled(every="5s")
    public void getValueFromAPI() throws IOException {
        try {
            MonitoringEntity monitoring = new MonitoringEntity();

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(new InputStreamReader(getInputStream().orElseThrow()), Map.class);

            var json = new JsonObject(jsonMap);

            monitoring.setDatetime(Timestamp.from(Instant.now()));
            monitoring.setDeliver(json.getJsonObject("backing_queue_status").getFloat("avg_ack_egress_rate"));
            monitoring.setPublish(json.getJsonObject("backing_queue_status").getFloat("avg_ack_ingress_rate"));
            monitoring.setMessages(json.getLong("messages"));

            Panache.<MonitoringEntity>withTransaction(monitoring::persist)
                    .onFailure().invoke(() -> this.logger.severe("ERROR while inserting in database monitoring"))
                    .await().indefinitely();

        } catch (Exception e)  {
            logger.warning("No log was inserted into the queue yet");
        }
    }

    public Optional<URL> urlMaker() {
        try {
            String sURL = "http://"
                    + this.rabbitmqurl
                    + ":" + this.rabbitmqport + "/api/queues/%2f/"
                    + QUEUE_NAME;

            return Optional.of(new URL(sURL));
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }

    public Optional<InputStream> getInputStream() {
        try {
            String userPass = "guest" + ":" + "guest";
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes());
            HttpURLConnection urlConnection = (HttpURLConnection) urlMaker().orElseThrow().openConnection();
            urlConnection.setRequestProperty("Authorization", basicAuth);
            return Optional.of((InputStream) urlConnection.getContent());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unsuccesfull connection at the database", e);
            return Optional.empty();
        }
    }
}
