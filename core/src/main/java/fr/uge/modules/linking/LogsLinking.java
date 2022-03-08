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
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.isNull;

public class LogsLinking {
    private static final Logger LOGGER = Logger.getLogger(LogsLinking.class.getName());
    private static final Comparator<Relation> datetimeComparator = Comparator.comparing(relation -> relation.target().datetime);

    LogsLinking() {}

    static {
        LOGGER.addHandler(new ConsoleHandler());
    }

    /**
     * Computes proximity between log of id1 and log of id2 for all the tokens in tokenTypes
     * @param log1
     * @param log2
     * @param delta
     * @return
     */
    public static Uni<TokensLink> computeLinks(LogEntity log1, LogEntity log2, long delta) {
        var map1 = fromLog(log1);
        var map2 = fromLog(log2);

        var datetimeLog1 = log1.datetime;
        var datetimeLog2 = log2.datetime;
        var datetimeComputation = TypeDatetime.computeDateTimeProximity(datetimeLog1, datetimeLog2, delta);

        var tokensLinks = map1.entrySet().stream().map(entry -> {
            var key = entry.getKey();
            var value = entry.getValue();

            var toCompare = map2.getOrDefault(key, new ArrayList<>());
            return TokenType.fromId(key).computeProximity(value, toCompare);
        }).toList();

        var finalComputations = tokensLinks.stream()
                .flatMap(tklink -> tklink.getComputations().stream())
                .toList();

        var finalTokensLink = new TokensLink(finalComputations, new AverageStrategy()).addComputation(datetimeComputation);
        return Uni.createFrom().item(finalTokensLink);
    }

    /***
     * Returns a Map of <TokenTypeId, List<TokenEntity>> that groups every type of token of a rootCause with its entities
     * @param log
     * @return a map of token entities associated with its type id
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
     * Retrieves all linked logs of a rootCause one within given delta
     * @param root
     * @param reportParameter
     * @return
     */
    public static Uni<GeneratedReport> linkedLogs(LogEntity root, ReportParameter reportParameter){
        LOGGER.log(Level.INFO, "Linking logs to {0}", root);
        if(isNull(root)) throw new NotYetTokenizedError();

        var reportGenerator = new ReportLinking();
        var datetime = root.datetime;
        return LogEntity.findAllWithJoin(
                    root.id,
                    Timestamp.valueOf(datetime.toLocalDateTime().minus(Duration.ofSeconds(reportParameter.delta()))),
                    datetime)
                .map(list -> reportGenerator.computeProximityTree(root, list.stream().distinct().toList(), reportParameter))
                .map(LogsLinking::fromRelationsTree)
                .invoke(generatedReport -> LOGGER.log(Level.INFO, "Generated report for id " + root.id + ": " + generatedReport))
                .onFailure().invoke(error -> LOGGER.severe("Error: " + error));
    }

    private static GeneratedReport fromRelationsTree(PriorityQueue<Relation> proximityQ){
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
