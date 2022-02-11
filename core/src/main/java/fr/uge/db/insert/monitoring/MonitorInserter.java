package fr.uge.db.insert.monitoring;

import com.google.gson.JsonParser;
import fr.uge.modules.api.server.entities.Monitoring;
import io.quarkus.scheduler.Scheduled;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Logger;


@ApplicationScoped
public class MonitorInserter {
    private static final String QUEUE_NAME = "logs";
    private final Properties properties = new Properties();
    private final Logger logger = Logger.getGlobal();

    @Inject
    EntityManager entityManager;

    @Scheduled(every="5s")
    public void getValueFromAPI() {
        String sURL = "http://"
                + properties.getProperty("MQMONITORINGSRV")
                + ":15672/api/exchanges/%2f/"
                + QUEUE_NAME
                + "?msg_rates_age=60&msg_rates_incr=5";

        try {
            URL url = new URL(sURL);
            String userPass = "guest" + ":" + "guest";
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes());//or
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.connect();

            var json = JsonParser.parseReader(new InputStreamReader((InputStream) urlConnection.getContent())).getAsJsonObject();
            var jsonTmp = json.get("incoming").getAsJsonArray().get(0).getAsJsonObject().get("stats").getAsJsonObject();
            var deliver = jsonTmp.get("confirm").getAsLong();
            var publish = jsonTmp.get("publish").getAsLong();
            var avg = jsonTmp.get("confirm_details").getAsJsonObject().get("avg_rate").getAsFloat();

            Monitoring monitoring = new Monitoring();
            monitoring.setDatetime(Timestamp.from(Instant.now()));
            monitoring.setDeliver(deliver);
            monitoring.setPublish(publish);
            monitoring.setAvg_rate(avg);
            entityManager.persist(monitoring);

        } catch (IOException e) {
            logger.severe("Error monitoring " + e);
        } catch (IndexOutOfBoundsException i) {
            logger.warning("Incorrect json schema, no data send yet.");
        }
    }
}
