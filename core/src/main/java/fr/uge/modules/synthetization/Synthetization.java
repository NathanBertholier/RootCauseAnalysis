package fr.uge.modules.synthetization;

import fr.uge.modules.api.model.report.GenericReport;
import fr.uge.modules.api.model.report.ReportResponse;
import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.api.model.report.ReportResponseExpanded;
import fr.uge.modules.linking.LogsLinking;
import fr.uge.modules.linking.token.type.TokenType;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Synthetization {

    private static final Logger LOGGER = Logger.getLogger(Synthetization.class.getName());

    static {
        LOGGER.addHandler(new ConsoleHandler());
    }

    public static Uni<GenericReport> getReport(long idLogTarget, ReportParameter reportParameter) {
        LOGGER.log(Level.INFO, "Retrieving rootCause cause for target: {0}", idLogTarget);
        return LogEntity.<LogEntity>findById(idLogTarget)
                .invoke(target -> LOGGER.log(Level.INFO, "Retrieved log target: {0}", target))
                .chain(target -> LogsLinking.linkedLogs(target, reportParameter)
                        .chain(generatedReport -> getMostSeenTokens(generatedReport.relevantLogs())
                                    .map(tokens -> {
                                        var relevantLogs = generatedReport.relevantLogs();
                                        var rootCause = generatedReport.rootCause();
                                        var report = new ReportResponse(rootCause, target, tokens, relevantLogs);
                                        if(!reportParameter.expanded()) {
                                            return report;
                                        }
                                        return new ReportResponseExpanded(report, generatedReport.computations());
                                    })
                        )
                );
    }

    private static Uni<TreeSet<TokensMostSeen>> getMostSeenTokens(List<LogEntity> logs) {
        Comparator<TokensMostSeen> comparator = Comparator.comparingLong(TokensMostSeen::count);
        return Multi.createFrom().items(logs.stream().flatMap(logEntity -> logEntity.tokens.stream()))
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
        var tokenTypeName = TokenType.fromId(entities.stream().findAny().orElseThrow().idtokentype).getName();
        var values = entities.stream()
                .collect(Collectors.groupingBy(TokenEntity::getValue, Collectors.counting()))
                .entrySet();
        var max = values.stream().max(Map.Entry.comparingByValue()).orElseThrow();
        var mostSeen = values.stream().filter(entry -> entry.getValue().equals(max.getValue())).map(Map.Entry::getKey).toList();

        return new TokensMostSeen(tokenTypeName, mostSeen, max.getValue());
    }
}
