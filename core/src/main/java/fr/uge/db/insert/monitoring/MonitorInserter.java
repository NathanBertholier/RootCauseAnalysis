package fr.uge.db.insert.monitoring;

import com.google.gson.JsonParser;
import fr.uge.modules.api.model.entities.Monitoring;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.scheduler.Scheduled;

import javax.enterprise.context.ApplicationScoped;
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

    MonitorInserter() throws IOException {
        this.properties.load(MonitorInserter.class.getClassLoader().getResourceAsStream("init.properties"));
    }

    @Scheduled(every="5s")
    public void getValueFromAPI() {
        Monitoring monitoring = new Monitoring();
        try {
            String sURL = "http://"
                    + this.properties.getProperty("MQMONITORINGSRV")
                    + ":15672/api/exchanges/%2f/"
                    + QUEUE_NAME;

            URL url = new URL(sURL);
            String userPass = "guest" + ":" + "guest";
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes());//or
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", basicAuth);

            var json = JsonParser.parseReader(new InputStreamReader((InputStream) urlConnection.getContent())).getAsJsonObject();
            var jsonTmp = json.get("incoming").getAsJsonArray().get(0).getAsJsonObject().get("stats").getAsJsonObject();
            var deliver = jsonTmp.get("confirm").getAsLong();
            var publish = jsonTmp.get("publish").getAsLong();
            var avg = jsonTmp.get("confirm_details").getAsJsonObject().get("rate").getAsFloat();

            monitoring.setDatetime(Timestamp.from(Instant.now()));
            monitoring.setDeliver(deliver);
            monitoring.setPublish(publish);
            monitoring.setAvgRate(avg);
        } catch (IOException e) {
            logger.warning(e + "");
            return;
        } catch (IndexOutOfBoundsException e)  {
            logger.warning("No log was inserted into the queue yet");
            return;
        }

        Panache.<Monitoring>withTransaction(monitoring::persist)
                .onFailure().invoke(() -> this.logger.severe("ERROR while inserting in database monitoring"))
                .await().indefinitely();
    }

}
