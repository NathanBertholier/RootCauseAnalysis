package fr.uge.modules.linking;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.Relation;
import fr.uge.modules.api.model.linking.TokensLink;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.linking.strategy.AverageStrategy;
import fr.uge.modules.linking.token.type.TokenType;
import fr.uge.modules.linking.token.type.TypeDatetime;
import org.jboss.logmanager.Level;

import java.util.*;
import java.util.function.Function;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;

public class ReportLinking {

    private static class TokenGrouperManager {
        private final Map<Long, Map<Integer, List<TokenEntity>>> fromId = new HashMap<>();

        public Map<Integer, List<TokenEntity>> groupTokens(LogEntity log){
            return fromId.computeIfAbsent(log.id, id -> groupMethod(log.tokens));
        }

        private Map<Integer, List<TokenEntity>> groupMethod(List<TokenEntity> tokenEntities){
            return tokenEntities.stream().collect(Collectors.groupingBy(
                    TokenEntity::getIdtokentype, mapping(Function.identity(), Collectors.toList())
            ));
        }
    }

    private static class RelationManager {
        private static final TokenGrouperManager grouperManager = new TokenGrouperManager();

        private Relation computeRelation(LogEntity firstLog, LogEntity secondLog){
            var firstMap = grouperManager.groupTokens(firstLog);
            var secondMap = grouperManager.groupTokens(secondLog);

            var computations = firstMap.entrySet().stream()
                    .map(entry -> {
                        var tokenTypeId = entry.getKey();
                        var tokenType = TokenType.fromId(tokenTypeId);
                        var entryList = entry.getValue();
                        var toCompareList = secondMap.getOrDefault(tokenTypeId, new ArrayList<>());
                        var link = tokenType.computeProximity(entryList, toCompareList);
                        return link;
                    })
                    .map(TokensLink::getComputations)
                    .flatMap(Collection::stream)
                    .toList();

            var finalRelation = new TokensLink(computations, new AverageStrategy());
            return new Relation(firstLog, secondLog, finalRelation);
        }

        public Relation addToRelation(Relation relation, Computation computation){
            return new Relation(relation.source(), relation.target(), relation.tokensLink().addComputation(computation));
        }
    }

    private static final RelationManager relationManager = new RelationManager();
    private static final Logger LOGGER = Logger.getLogger(ReportLinking.class.getName());

    static {
        LOGGER.addHandler(new ConsoleHandler());
    }

    public TreeSet<Relation> computeProximityTree(LogEntity logTarget, List<LogEntity> logWithinDelta, ReportParameter rp){
        TreeSet<Relation> redBlack = new TreeSet<>(Comparator.comparingDouble(relation -> - relation.tokensLink().getProximity()));
        var targetDatetime = logTarget.datetime;

        var delta = rp.delta();
        var proximityLimit = rp.proximity_limit();
        var networkSize = rp.network_size();

        logWithinDelta.stream()
                .map(log -> {
                    var relation = relationManager.computeRelation(logTarget, log);
                    Computation datetimeComputation = TypeDatetime.computeDateTimeProximity(log.datetime, targetDatetime, delta);
                    relationManager.addToRelation(relation, datetimeComputation);

                    LOGGER.log(Level.DEBUG, "Relation created between " + logTarget + " and " + log + ": " + relation);
                    return relation;
                })
                .forEach(relation -> {;
                    var relationProximity = relation.tokensLink().getProximity();
                    if(!redBlack.isEmpty()) {
                        var firstProximity = redBlack.first().tokensLink().getProximity();
                        if(relationProximity > proximityLimit){
                            if(redBlack.size() == networkSize){
                                if(relationProximity > firstProximity){
                                    redBlack.pollFirst();
                                    redBlack.add(relation);
                                }
                            } else redBlack.add(relation);
                        }
                    } else redBlack.add(relation);
                });

        LOGGER.log(Level.DEBUG, "Generated tree for id " + logTarget.id + ": " + redBlack);
        return redBlack;
    }
}
