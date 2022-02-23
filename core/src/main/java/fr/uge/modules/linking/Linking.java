package fr.uge.modules.linking;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.linking.token.type.*;
import io.smallrye.mutiny.Uni;

import java.sql.*;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

public class Linking {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final HashMap<Integer, TokenType> tokensType = new HashMap<>();

    public Linking() {
        this.addInTokensType(TokenType.TokenTypeId.ID_IPV4.getId(), new TypeIPv4());
        this.addInTokensType(TokenType.TokenTypeId.ID_STATUS.getId(), new TypeHTTPStatus());
        this.addInTokensType(TokenType.TokenTypeId.ID_EDGERESPONSE.getId(), new TypeEdgeResponse());
        this.addInTokensType(TokenType.TokenTypeId.ID_IPV6.getId(), new TypeIPv6());
    }

    private void addInTokensType(Integer id, TokenType tokenType) {
        tokensType.compute(id, (k,v) -> tokenType);
    }

    public Uni<SortedMap<Float, LogEntity>> linkReport(long id, ReportParameter rp) {
        return LogEntity.<LogEntity>findById(id)
                .chain(completeLog -> {
                    var datetime = completeLog.datetime;
                    var start = Timestamp.valueOf(datetime.toLocalDateTime().minus(Duration.ofSeconds(rp.delta())));
                    return LogEntity.<LogEntity>find("id != ?1 and datetime between ?2 and ?3", id,
                                    start, datetime).list()
                            .map(list -> computeProximityTree(completeLog, list, rp));
                });
    }


    private void fillHashmap(List<TokenEntity> tokens, HashMap<Integer, List<TokenEntity>> tokensToFill) {
        tokens.forEach(token -> {
            tokensToFill.computeIfAbsent(token.getIdtokentype(),
                    k -> new ArrayList<>());
            tokensToFill.get(token.getIdtokentype()).add(token);
        });
    }

    public SortedMap<Float, LogEntity> computeProximityTree(LogEntity logTarget, List<LogEntity> logWithinDelta, ReportParameter rp){
        TreeMap<Float, LogEntity> redBlack = new TreeMap<>(Collections.reverseOrder());
        var targetDatetime = logTarget.getDatetime();

        HashMap<Integer, List<TokenEntity>> tokenTarget = new HashMap<>();
        fillHashmap(logTarget.getTokens(), tokenTarget);

        var delta = rp.delta();

        HashMap<Integer, List<TokenEntity>> tokenToLink = new HashMap<>();
        this.tokensType.forEach((id,v) -> tokenToLink.computeIfAbsent(id, k -> new ArrayList<>()));

        logWithinDelta.forEach(log -> {
            float proximity = TypeDatetime.computeDateTimeProximity(log.getDatetime().toLocalDateTime(), targetDatetime.toLocalDateTime(), delta);
            log.getTokens().forEach(token -> tokenToLink.get(token.getIdtokentype()).add(token));

            proximity += tokenTarget.keySet().stream()
                    .mapToDouble((k) -> tokensType.get(k)
                            .computeProximity(tokenTarget.get(k),
                    tokenToLink.get(k))).sum();

            proximity /= (tokenTarget.size() + 1); // NUMBER OF TOKENS CONSIDERATE + TIMESTAMP

            System.out.println(proximity);
            if(redBlack.size() > rp.network_size() - 1) {
                if (proximity > redBlack.lastKey()) {
                    redBlack.pollLastEntry();
                    redBlack.put(proximity, log);
                }
            } else {
                redBlack.put(proximity, log);
            }
            tokenToLink.forEach((k,v) -> v.clear());
        });
        redBlack.forEach((k,v) -> {
            System.out.println(k);
            System.out.println(v.getTokens().get(1));
        });
        return redBlack;
    }
}
