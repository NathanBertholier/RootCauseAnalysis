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
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Synthetization {
    private static final Logger LOGGER = Logger.getLogger(Synthetization.class.getName());

    Synthetization() {}

    /**
     * Generate a report from the logTarget and parameters
     * @param idLogTarget       Log used to create a report
     * @param reportParameter   Parameters used for the report
     * @return                  An object implementing the GenericReport interface, depending on the parameters
     */
    public static Uni<GenericReport> getReport(long idLogTarget, ReportParameter reportParameter) {
        LOGGER.info(() -> "Retrieving rootCause cause for target: " + idLogTarget);
        return LogEntity.<LogEntity>findById(idLogTarget)
                .invoke(target -> LOGGER.info(() -> "Retrieved log target: " + target))
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

    /**
     * Get the most seen tokens among a list of logEntity
     * @param logs  List of LogEntity used to iterate and found most seed tokens
     * @return      A treeSet of the most token seen.
     */
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
                .invoke(set -> LOGGER.info(() -> "Most tokens seen: " + set));
    }

    /**
     * Get the most seen token among a TokenType.
     * @param entities  List of entities with the same tokenType.
     * @return          A TokensMostSeen record that represent a tokenType, a list of value that have the same count and a count.
     */
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
