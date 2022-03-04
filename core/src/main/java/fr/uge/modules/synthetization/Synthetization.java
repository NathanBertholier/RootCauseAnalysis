package fr.uge.modules.synthetization;

import fr.uge.modules.api.model.ReportResponse;
import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.linking.LogsLinking;
import fr.uge.modules.linking.token.type.TokenType;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.*;
import java.util.logging.Logger;

public class Synthetization {

    private static final Logger LOGGER = Logger.getLogger(Synthetization.class.getName());

    public static Uni<ReportResponse> getReport(long idRootLog, ReportParameter reportParameter) {
        return LogEntity.<LogEntity>findById(idRootLog)
                .chain(log -> LogsLinking.linkedLogs(log, reportParameter)
                        .chain(generatedReport -> getMostSeenTokens(generatedReport.entities().values())
                                .map(tokens -> new ReportResponse(generatedReport.rootCause(), tokens, generatedReport.entities()))
                        )
                );
    }

    private static Uni<Set<TokensMostSeen>> getMostSeenTokens(Collection<LogEntity> logs) {
        Comparator<TokensMostSeen> comparator = Comparator.comparingLong(TokensMostSeen::count);
        return Multi.createFrom().items(logs.stream().flatMap(log -> log.tokens.stream()))
                .group().by(TokenEntity::getIdtokentype)
                .collect().in(() -> new TreeSet<>(comparator), (set, group) -> group.collect()
                        .asList()
                        .map(Synthetization::fromTokenEntities)
                        .subscribeAsCompletionStage()
                        .whenComplete(((tokensMostSeen, error) -> {
                            if(error != null) LOGGER.severe("Error while isnerting " + tokensMostSeen + ": " + error);
                            else set.add(tokensMostSeen);
                        })));
    }

    private static TokensMostSeen fromTokenEntities(List<TokenEntity> entities){
        var tokenTypeName = TokenType.fromId(entities.stream().findAny().orElseThrow().idtokentype).getName();
        var values = entities.stream().map(t -> t.value).toList();
        var size = entities.size();
        return new TokensMostSeen(tokenTypeName, values, size);
    }
}
