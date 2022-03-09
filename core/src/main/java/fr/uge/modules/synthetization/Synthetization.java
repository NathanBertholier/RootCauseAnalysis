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

/**
 * Class used to perform report generations
 */
public class Synthetization {

    Synthetization() {}

    private static final Logger LOGGER = Logger.getLogger(Synthetization.class.getName());

    /**
     * Method used to perform report generations according to a targeted log id and report parameters
     * @param idLogTarget the targeted log id
     * @param reportParameter the report parameters used to generate report
     * @return
     */
    public static Uni<GenericReport> getReport(long idLogTarget, ReportParameter reportParameter) {
        LOGGER.info(() -> "Retrieving rootCause cause for target: " + idLogTarget);
        // Retrieve the targeted log from the DB
        return LogEntity.<LogEntity>findById(idLogTarget)
                .invoke(target -> LOGGER.info(() -> "Retrieved log target: " + target))
                // When retrieved, retrieves every linked logs according to the given parameters
                .chain(target -> LogsLinking.linkedLogs(target, reportParameter)
                        // When retrieved, groups tokens according to how many times they have been seen
                        .chain(generatedReport -> getMostSeenTokens(generatedReport.relevantLogs())
                                // When grouped, generates a simple or expanded report
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
     * Retrieves most seen tokens according to relevant logs
     * @param logs List of every relevant logs to a targeted one
     * @return a TreeSet containing MostTokensSeen objects, ordered according to their count value
     */
    private static Uni<TreeSet<TokensMostSeen>> getMostSeenTokens(List<LogEntity> logs) {
        // Comparator used to sort MostTokensSeen objects according to how many times they've been seen
        Comparator<TokensMostSeen> comparator = Comparator.comparingLong(TokensMostSeen::count);
        // Flats every tokens in an object in order to group them according to the comparator
        return Multi.createFrom().items(logs.stream().flatMap(logEntity -> logEntity.tokens.stream()))
                .group().by(TokenEntity::getIdtokentype)
                // Collects them in a treeset
                .collect().in(() -> new TreeSet<>(comparator), (set, group) -> group.collect()
                        .asList()
                        .map(Synthetization::fromTokenEntities)
                        .subscribeAsCompletionStage()
                        .thenAccept((set::add))
                )
                .invoke(set -> LOGGER.info(() -> "Most tokens seen: " + set));
    }

    /**
     * Method used to create a TokensMostSeen object according to a list of token entities
     * @param entities a list of TokenEntity, contained in a specific log
     * @return a TokensMostSeen object
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
