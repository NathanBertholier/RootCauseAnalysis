package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.CompleteLog;
import fr.uge.modules.api.model.TokenRequest;
import fr.uge.modules.api.model.TokensResponse;
import fr.uge.modules.api.model.entities.LogEntity;
import io.smallrye.mutiny.Uni;

import java.sql.Timestamp;
import java.util.List;

public class TokenRetriever {
    public static Uni<List<LogEntity>> getTokens(TokenRequest tokenRequest){
        var start = Timestamp.valueOf(tokenRequest.init_datetime());
        var end = Timestamp.valueOf(tokenRequest.end_datetime());
        var rows = tokenRequest.rows();
        var id = tokenRequest.id();

        return LogEntity.retrieveLogs(id, start, end, rows);
    }

    public static Uni<TokensResponse> fromLogs(Uni<List<LogEntity>> logs){
        return logs.map(list -> list.stream()
                .map(log -> new CompleteLog(log.getId(), log.getRawLog().getLog(), log.datetime.toLocalDateTime(), log.tokens))
                .toArray(CompleteLog[]::new)
        ).map(TokensResponse::new);
    }
}
