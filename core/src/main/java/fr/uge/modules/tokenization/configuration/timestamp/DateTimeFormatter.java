package fr.uge.modules.tokenization.configuration.timestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface DateTimeFormatter {
    Optional<Timestamp> getTimestamp(List<String> words);
}
