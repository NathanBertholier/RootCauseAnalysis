package fr.uge.modules.synthetization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.uge.db.insert.monitoring.MonitorInserter;
import fr.uge.modules.api.model.ReportResponse;
import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.CompleteLog;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.RawLogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.report.ReportParameter;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.hibernate.criterion.Projections;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Synthetization {
    private static ObjectMapper mapper = new ObjectMapper();
    private static final Properties PROPERTIES = new Properties();
    private static final Logger LOGGER = Logger.getGlobal();

    static {
        try {
            PROPERTIES.load(MonitorInserter.class.getClassLoader().getResourceAsStream("init.properties"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException", e);
        }
    }

    @Blocking
    public static Uni<ReportResponse> getReport(long idRootLog, ReportParameter reportParameter) {
        return LogEntity
                .<LogEntity>findById(idRootLog)
                .onFailure().recoverWithUni(error -> Uni.createFrom().failure(NoSuchElementException::new))
                .map(rootLog -> {
                    var logRoot = new CompleteLog(rootLog.id, rootLog.datetime, rootLog.rawLog.log, rootLog.tokens);
                    var logsChildren = fetchChildren()
                    return getMostSeenTokens(idRootLog)
                            .map(map -> map
                                    .entrySet().stream()
                                    .map(entry -> new TokensMostSeen(
                                            entry.getValue().token_type(),
                                            entry.getValue().token_value(),
                                            entry.getKey()))
                                    .collect(Collectors.toSet()))
                            .map(mostSeen -> new ReportResponse(logRoot, mostSeen, ));
                    return new ReportResponse(logRoot, tokensMostSeen, null);
                });
    }

    private static Uni<Map<Long, TokensMostSeen>> getMostSeenTokens(long idRootLog) {
        return TokenEntity.find("idlog = ?1", idRootLog)
                .<TokenEntity>stream()
                .group().by(TokenEntity::getValue)
                .toUni().map(group -> new TreeMap(Comparator.naturalOrder()));
        /*
        ArrayList<Token> list = new ArrayList<>();
        ArrayList<TokensMostSeen> tokens = new ArrayList<>();
        map.forEach((k, v) -> list.addAll(v.getTokens()));
        var groupByType = list.stream().
                collect(Collectors.groupingBy(t -> t.getType().getName()));
        var numberByToken = groupByType.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e ->
                        e.getValue().stream().collect(Collectors.groupingBy(Token::getValue,
                                        Collectors.counting())).entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
        numberByToken.forEach((k, v) -> {
            Map<Long, List<String>> value = new HashMap<>();
            v.forEach((k2, v2) -> {
                if (value.containsKey(v2)) {
                    value.get(v2).add(k2);
                } else {
                    var x = new ArrayList<String>();
                    x.add(k2);
                    value.put(v2, x);
                }
            });
            var sortedlist = value.entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByKey())).collect(Collectors.toMap(Map.Entry::getKey,
                            Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            Map.Entry<Long, List<String>> entry2 = sortedlist.entrySet().iterator().next();
            String[] tabstring = entry2.getValue().toArray(String[]::new);
            tokens.add(new TokensMostSeen(k,tabstring,entry2.getKey()));
        });
         */
    }

    private static ArrayNode getProximity(SortedMap<Float, CompleteLog> map) {
        ArrayNode prox = mapper.createArrayNode();
        map.forEach((k, v) -> {
            ObjectNode log = mapper.createObjectNode();
            log.put("id", v.id());
            log.put("proximity", k);
            prox.add(log);
        });
        return prox;
    }

    private static CompleteLog[] fetchChildren() {
        return null;
    }
}
