package fr.uge.modules.linking;

import fr.uge.modules.api.model.CompleteLog;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.linking.LinksResponse;
import fr.uge.modules.linking.strategy.LinkingStrategy;
import io.smallrye.mutiny.Uni;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;

// TODO
public class LogsLinking {
    /**
     * Computes proximity between log of id1 and log of id2 for all the tokens in tokenTypes
     */
    public static Uni<LinksResponse> computeLinks(Uni<CompleteLog> log1, Uni<CompleteLog> log2, LinkingStrategy strategy) {
        return strategy.computeLinks(log1, log2)
                .map(result -> new LinksResponse(null, result));
    }

    /**
     * Retrieves all linked logs of a root one within given delta
     * @param id
     * @param delta
     * @return
     */
    public static Uni<List<LogEntity>> linkedLogs(LogEntity root, long delta){
        var datetime = root.datetime;
        return LogEntity.<LogEntity>find("id != ?1 and datetime between ?2 and ?3", root.id,
                        Timestamp.valueOf(datetime.toLocalDateTime().minus(Duration.ofSeconds(delta))),
                        datetime).list();
    }
}
