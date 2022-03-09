package fr.uge.modules.linking;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Relation;
import fr.uge.modules.api.model.linking.TokensLink;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.error.EmptyReportError;
import fr.uge.modules.error.NotYetTokenizedError;
import fr.uge.modules.linking.strategy.AverageStrategy;
import fr.uge.modules.linking.token.type.TokenType;
import fr.uge.modules.linking.token.type.TypeDatetime;
import fr.uge.modules.synthetization.GeneratedReport;
import io.smallrye.mutiny.Uni;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

import static java.util.Objects.isNull;

/**
 * Class used to compute relations between different logs
 */
public class LogsLinking {
    private static final Logger LOGGER = Logger.getLogger(LogsLinking.class.getName());
    private static final Comparator<Relation> datetimeComparator = Comparator.comparing(relation -> relation.target().datetime);

    LogsLinking() {}

    /**
     * Computes proximity between two given logs
     * @param log1 LogEntity object representing the first log to compare
     * @param log2 LogEntity object representing the second log to compare
     * @param delta long value representing the time variable used in the datetime token proximity computation
     * @return An Uni<TokensLink> representing the relation between those two logs
     */
    public static Uni<TokensLink> computeLinks(LogEntity log1, LogEntity log2, long delta) {
        if(log1 == null || log2 == null) return Uni.createFrom().failure(new NotYetTokenizedError());

        // Group tokens according to their tokentype_id
        var map1 = fromLog(log1);
        var map2 = fromLog(log2);

        // Compute the time proximity
        var datetimeLog1 = log1.datetime;
        var datetimeLog2 = log2.datetime;
        var datetimeComputation = TypeDatetime.computeDateTimeProximity(datetimeLog1, datetimeLog2, delta);

        // Compute every other tokens proximities and group them in a list in order to retrieve every computation
        var tokensLinks = map1.entrySet().stream().map(entry -> {
            var key = entry.getKey();
            var value = entry.getValue();

            var toCompare = map2.getOrDefault(key, new ArrayList<>());
            return TokenType.fromId(key).computeProximity(value, toCompare);
        }).toList();

        // Retrieve every computation of the tokenslink objects
        var finalComputations = tokensLinks.stream()
                .flatMap(tklink -> tklink.getComputations().stream())
                .toList();

        // Add the datetime computation to the computations list and create a final TokensLink to return
        var finalTokensLink = new TokensLink(finalComputations, new AverageStrategy()).addComputation(datetimeComputation);
        return Uni.createFrom().item(finalTokensLink);
    }

    /***
     * Returns a Map of <TokenTypeId, List<TokenEntity>> that groups every type of token of a rootCause with its objects,
     * according to their tokentype_id
     * @param log LogEntity representing the container of the tokens
     * @return a map of token entities according to their tokentype_id
     */
    private static Map<Integer, List<TokenEntity>> fromLog(LogEntity log){
        var map = new HashMap<Integer, List<TokenEntity>>();
        log.tokens.forEach(token -> map.compute(token.idtokentype, (tokenType, entities) -> {
            if(entities == null) entities = new ArrayList<>();
            entities.add(token);
            return entities;
        }));
        return map;
    }

    /**
     * Retrieves all linked logs of a targeted one according to the report parameters
     * @param target the targeted log
     * @param reportParameter the report generation parameters
     * @return a GeneratedReport containing information about the report
     */
    public static Uni<GeneratedReport> linkedLogs(LogEntity target, ReportParameter reportParameter){
        LOGGER.info(() -> "Linking logs to " + target);
        if(isNull(target)) throw new NotYetTokenizedError();

        var reportGenerator = new ReportLinking();
        var datetime = target.datetime;

        // Retrieve every linked logs to the targeted one
        return LogEntity.findAllWithJoin(
                    target.id,
                    Timestamp.valueOf(datetime.toLocalDateTime().minus(Duration.ofSeconds(reportParameter.delta()))),
                    datetime)
                // Compute the proximity tree between the related logs and the targeted one
                .map(list -> reportGenerator.computeProximityQueue(target, list.stream().distinct().toList(), reportParameter))
                // Create a GeneratedReport object according to the returned queue
                .map(LogsLinking::fromRelationQueue)
                .invoke(generatedReport -> LOGGER.info(() -> "Generated report for id " + target.id + ": " + generatedReport))
                .onFailure().invoke(error -> LOGGER.severe(() -> "Error: " + error));
    }

    /**
     * Creates a GeneratedReport object according to the relation priority queue
     * @param proximityQ the relation priority queue of the related logs
     * @return a GeneratedReport containing information about the generated report
     */
    private static GeneratedReport fromRelationQueue(PriorityQueue<Relation> proximityQ){
        var rootcause = proximityQ.stream()
                .min(datetimeComparator)
                .orElseThrow(EmptyReportError::new)
                .target();
        var relevantLogs = proximityQ.stream()
                .map(Relation::target)
                .toList();
        return new GeneratedReport(rootcause, relevantLogs, proximityQ);
    }
}
