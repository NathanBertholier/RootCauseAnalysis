package fr.uge.modules.linking;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Relation;
import fr.uge.modules.api.model.linking.TokensLink;
import fr.uge.modules.api.model.report.GenericReport;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.error.EmptyReportError;
import fr.uge.modules.error.NotYetTokenizedError;
import fr.uge.modules.linking.token.type.TokenType;
import fr.uge.modules.synthetization.GeneratedReport;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

// TODO
public class LogsLinking {
    private static final Logger LOGGER = Logger.getLogger(LogsLinking.class.getName());
    private static final Comparator<Relation> datetimeComparator = Comparator.comparing(relation -> relation.target().datetime);

    static {
        LOGGER.addHandler(new ConsoleHandler());
    }

    /**
     * Computes relations between log of id1 and log of id2 for all the tokens in tokenTypes
     * @param log1
     * @param log2
     * @param delta
     * @return
     */
    public static Uni<TokensLink> computeLinks(LogEntity log1, LogEntity log2, long delta) {
        var map1 = fromLog(log1);
        var map2 = fromLog(log2);

        return Uni.createFrom().item(map1.entrySet().stream().mapToDouble((entry) -> {
            var key = entry.getKey();
            var value = entry.getValue();

            var toCompare = map2.getOrDefault(key, new ArrayList<>());
            return TokenType.fromId(key).computeProximity(value, toCompare).getProximity();
        }).sum()).map(TokensLink::withoutStrategy);
    }

    /***
     * Returns a Map of <TokenTypeId, List<TokenEntity>> that groups every type of token of a root with its entities
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
     * Retrieves all linked logs of a root one within given delta
     * @param root
     * @param reportParameter
     * @return
     */
    public static Uni<GeneratedReport> linkedLogs(LogEntity root, ReportParameter reportParameter){
        LOGGER.log(Level.INFO, "Linking logs to {0}", root);
        if(isNull(root)) throw new NotYetTokenizedError();

        var reportGenerator = new ReportLinking();
        var datetime = root.datetime;
        return LogEntity.<LogEntity>find("id != ?1 and datetime between ?2 and ?3",
                    root.id,
                    Timestamp.valueOf(datetime.toLocalDateTime().minus(Duration.ofSeconds(reportParameter.delta()))),
                    datetime).list()
                .map(list -> reportGenerator.computeProximityTree(root, list, reportParameter))
                .map(LogsLinking::fromRelationsTree)
                .invoke(generatedReport -> LOGGER.log(Level.INFO, "Generated report for id " + root.id + ": " + generatedReport))
                .onFailure().invoke(error -> LOGGER.severe("Error: " + error));
    }

    private static GeneratedReport fromRelationsTree(TreeSet<Relation> treeset){
        var rootcause = treeset.stream()
                .max(datetimeComparator)
                .orElseThrow(EmptyReportError::new)
                .target();
        var relevantLogs = treeset.stream()
                .map(Relation::target)
                .toList();
        return new GeneratedReport(rootcause, relevantLogs, treeset);
    }
}
