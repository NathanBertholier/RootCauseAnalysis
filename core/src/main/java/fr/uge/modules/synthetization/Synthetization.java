package fr.uge.modules.synthetization;

import fr.uge.modules.api.model.ReportResponse;
import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.linking.LogsLinking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

public class Synthetization {
    private static final Logger LOGGER = Logger.getLogger(Synthetization.class.getName());

    public static Uni<ReportResponse> getReport(long idRootLog, ReportParameter reportParameter) {
        return LogEntity.<LogEntity>findById(idRootLog)
                .chain(log -> LogsLinking.linkedLogs(log, reportParameter.delta())
                        .chain(logEntities -> getMostSeenTokens(logEntities)
                                .map(sorted -> new ReportResponse(log, sorted, logEntities))
                        )
                );
    }

    private static Uni<TreeMap<Integer, TokensMostSeen>> getMostSeenTokens(List<LogEntity> logs) {
        return Multi.createFrom().items(logs.stream().flatMap(log -> log.tokens.stream()))
                .group().by(TokenEntity::getIdtokentype)
                .collect().in(TreeMap::new, ((map, multi) -> {
                    var idTokenType = multi.key();
                    multi.collect()
                            .asList()
                            .map(Synthetization::fromTokenEntities)
                            .subscribeAsCompletionStage()
                            .whenComplete(
                                    (tokensMostSeen, error) -> {
                                        if(error != null) LOGGER.severe("Error: " + error);
                                        else map.putIfAbsent(idTokenType, tokensMostSeen);
                                    });
                }));
    }

    private static TokensMostSeen fromTokenEntities(List<TokenEntity> entities){
        var tokenTypeName = entities.stream().findAny().orElseThrow().token_type.name;
        var values = entities.stream().map(t -> t.value).toList();
        var size = entities.size();
        return new TokensMostSeen(tokenTypeName, values, size);
    }
}
