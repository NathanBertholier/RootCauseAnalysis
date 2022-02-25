package fr.uge.modules.synthetization;

import fr.uge.modules.api.model.ReportResponse;
import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.error.EmptyReportError;
import fr.uge.modules.error.NotYetTokenizedError;
import fr.uge.modules.linking.LogsLinking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Synthetization {

    private static final Logger LOGGER = Logger.getLogger(Synthetization.class.getName());

    static {
        LOGGER.addHandler(new ConsoleHandler());
    }

    public static Uni<ReportResponse> getReport(long idLogTarget, ReportParameter reportParameter) {
        LOGGER.log(Level.INFO, "Retrieving root cause for target: {0}", idLogTarget);
        return LogEntity.<LogEntity>findById(idLogTarget)
                .invoke(log -> LOGGER.log(Level.INFO, "Retrieved log target: {0}", log))
                .chain(log -> LogsLinking.linkedLogs(log, reportParameter)
                        .chain(generatedReport -> getMostSeenTokens(generatedReport.entities().values())
                                .map(tokens -> new ReportResponse(generatedReport.rootCause(), tokens, generatedReport.entities()))
                        )
                );
    }

    private static Uni<TreeSet<TokensMostSeen>> getMostSeenTokens(Collection<LogEntity> logs) {
        Comparator<TokensMostSeen> comparator = Comparator.comparingLong(TokensMostSeen::count);
        return Multi.createFrom().items(logs.stream().flatMap(log -> log.tokens.stream()))
                .group().by(TokenEntity::getIdtokentype)
                .collect().in(() -> new TreeSet<>(comparator), (set, group) -> group.collect()
                        .asList()
                        .map(Synthetization::fromTokenEntities)
                        .subscribeAsCompletionStage()
                        .thenAccept((set::add))
                )
                .invoke(set -> LOGGER.log(Level.INFO, "Most tokens seen: {0}", set));
    }

    private static TokensMostSeen fromTokenEntities(List<TokenEntity> entities){
        var tokenTypeName = entities.stream().findAny().orElseThrow().token_type.name;
        var values = entities.stream().map(t -> t.value).toList();
        var size = entities.size();
        return new TokensMostSeen(tokenTypeName, values, size);
    }
}
