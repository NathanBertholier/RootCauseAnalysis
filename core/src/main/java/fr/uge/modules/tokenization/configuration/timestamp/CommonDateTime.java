package fr.uge.modules.tokenization.configuration.timestamp;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommonDateTime implements DateTimeFormatter{

    private final SimpleDateFormat formatter = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss", new Locale("en", "US"));

    @Override
    public Optional<Timestamp> getTimestamp(List<String> words) {
        Optional<Timestamp> res;
        for(String word : words){
            try {
                res = Optional.of(new Timestamp(formatter.parse(word).getTime()));
            } catch (ParseException e) {
                continue;
            }
            return res;
        }
        return Optional.empty();
    }

}
