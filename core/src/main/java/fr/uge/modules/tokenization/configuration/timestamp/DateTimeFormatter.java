package fr.uge.modules.tokenization.configuration.timestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Interface used in profile in order to retrieve datetime.
 */
public interface DateTimeFormatter {
    Optional<Timestamp> getTimestamp(List<String> words);
}
