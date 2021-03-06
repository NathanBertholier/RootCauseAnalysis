package fr.uge.modules.tokenization.configuration.timestamp;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

/**
 * Used in a profile to found the DateTime of a given log.
 * AWS profile takes the first and second word of the log.
 */
public class AWSDateTime implements DateTimeFormatter {
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Optional<Timestamp> getTimestamp(List<String> words) {
        try {
            int timeIndex = 1;
            int dateIndex = 0;
            return Optional.of(new Timestamp(this.formatter.parse(words.get(dateIndex) + " " + words.get(timeIndex))
                    .getTime()));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }
}
