package fr.uge.modules.synthetization;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.uge.modules.api.model.CompleteLog;
import fr.uge.modules.api.model.ReportResponse;
import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.TokensResponse;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.entities.TokenTypeEntity;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.linking.LogsLinking;
import fr.uge.modules.linking.ReportLinking;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.GroupedMulti;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Synthetization {
    private static final ReportLinking reportLinking = new ReportLinking();
    private static final Logger LOGGER = Logger.getLogger(Synthetization.class.getName());

    public static Uni<ReportResponse> getReport(long idRootLog, ReportParameter reportParameter) {
        return LogEntity.<LogEntity>findById(idRootLog)
                .chain(log -> LogsLinking.linkedLogs(log, reportParameter.delta())
                        .chain(logEntities -> getMostSeenTokens(logEntities)
                                .map(sorted -> new ReportResponse(log, sorted, logEntities))));
    }

    private static Uni<TreeMap<Integer, TokensMostSeen>> getMostSeenTokens(List<LogEntity> logs) {
        return Multi.createFrom().items(logs.stream().flatMap(log -> log.tokens.stream()))
                .group().by(TokenEntity::getIdtokentype)
                .collect().in(TreeMap::new, ((map, multi) -> {
                    var idTokenType = multi.key();
                    multi.collect().asList().chain(entities -> {
                        var tokenTypeName = entities.stream().findAny().orElseThrow().token_type.name;
                        var values = entities.stream().map(t -> t.value).toList();
                        var size = entities.size();
                        return Uni.createFrom().item(new TokensMostSeen(tokenTypeName, values, size));
                    }).invoke(tokensMostSeen -> map.put(idTokenType, tokensMostSeen)).await().indefinitely();
                }));
    }

    private static ArrayNode getProximity(SortedMap<Float, CompleteLog> map) {
    /*
        ArrayNode prox = mapper.createArrayNode();
        map.forEach((k, v) -> {
            ObjectNode log = mapper.createObjectNode();
            log.put("id", v.id());
            log.put("proximity", k);
            prox.add(log);
        });
        return prox;
     */
        return null;
    }

    private static CompleteLog[] fetchChildren() {
        return null;
    }
}
