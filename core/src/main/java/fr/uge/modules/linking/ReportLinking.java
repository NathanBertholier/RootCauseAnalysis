package fr.uge.modules.linking;

import fr.uge.modules.api.model.CompleteLog;
import fr.uge.modules.api.model.ReportResponse;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.linking.token.type.*;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

public class ReportLinking {
    private final HashMap<Integer, TokenType> tokensType = new HashMap<>();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public ReportLinking() {
        this.addInTokensType(TokenType.TokenTypeId.ID_IPV4.getId(), new TypeIPv4());
        this.addInTokensType(TokenType.TokenTypeId.ID_IPV6.getId(), new TypeIPv6());
        this.addInTokensType(TokenType.TokenTypeId.ID_DATETIME.getId(), new TypeDatetime());
        this.addInTokensType(TokenType.TokenTypeId.ID_EDGERESPONSE.getId(), new TypeEdgeResponse());
        this.addInTokensType(TokenType.TokenTypeId.ID_STATUS.getId(), new TypeHTTPStatus());
    }

    private void addInTokensType(Integer id, TokenType tokenType) {
        tokensType.compute(id, (k,v) -> tokenType);
    }

    public Uni<ReportResponse> linkReport(long id, ReportParameter reportParameter) {
        return LogEntity.<LogEntity>findById(id)
                .chain(root ->
                    LogsLinking.linkedLogs(root, reportParameter)
                            .map(set -> new ReportResponse(root, new HashSet<>(), set)) // pour merge
                );
    }

    private void fillHashmap(List<TokenEntity> tokens, HashMap<Integer, List<TokenEntity>> tokensToFill) {
        tokens.forEach(token -> tokensToFill
                .computeIfAbsent(token.getIdtokentype(), ArrayList::new)
                .add(token)
        );
    }

    public SortedMap<Double, LogEntity> computeProximityTree(LogEntity logTarget, List<LogEntity> logWithinDelta, ReportParameter rp){
        TreeMap<Double, LogEntity> redBlack = new TreeMap<>(Collections.reverseOrder());
        var targetDatetime = logTarget.datetime;

        HashMap<Integer, List<TokenEntity>> tokenTarget = new HashMap<>();
        fillHashmap(logTarget.tokens, tokenTarget);

        var delta = rp.delta();

        HashMap<Integer, List<TokenEntity>> tokenToLink = new HashMap<>();
        this.tokensType.forEach((id,v) -> tokenToLink.computeIfAbsent(id, k -> new ArrayList<>()));

        logWithinDelta.forEach(log -> {
            double proximity = TypeDatetime.computeDateTimeProximity(log.datetime, targetDatetime, delta);
            log.getTokens().forEach(token -> tokenToLink.get(token.getIdtokentype()).add(token));

            proximity += tokenTarget.keySet().stream()
                    .mapToDouble((k) -> tokensType.get(k)
                            .computeProximity(tokenTarget.get(k),
                                    tokenToLink.get(k))).sum();

            proximity /= (tokenTarget.size() + 1); // NUMBER OF TOKENS CONSIDERATE + TIMESTAMP

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
        redBlack.forEach((k,v) -> System.out.println(k + " LOG " + v));
        redBlack.forEach((k,v) -> System.out.println(k));
        return redBlack;
    }

}
