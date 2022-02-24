package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.TokenRequest;
import fr.uge.modules.api.model.entities.LogEntity;
import io.smallrye.mutiny.Uni;

import java.sql.Timestamp;
import java.util.List;

public class TokenRetriever {
    // STart not empty -> all from date to today
    // End not empty -> all from begingin to end value
    public static Uni<List<LogEntity>> getTokens(TokenRequest tokenRequest){
        var start = Timestamp.valueOf(tokenRequest.init_datetime());
        var end = Timestamp.valueOf(tokenRequest.end_datetime());
        var rows = tokenRequest.rows();
        var id = tokenRequest.id();

        var betweenDates = LogEntity.retrieveLogs(id, start, end, rows);
        return betweenDates;
    }
}
