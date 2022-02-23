package fr.uge.modules.linking;

import fr.uge.modules.api.model.CompleteLog;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.linking.token.type.*;

import java.sql.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Linking {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final HashMap<Integer, TokenType> tokensType = new HashMap<>();
    private final List<LogEntity> logs = new ArrayList<>(); //Logs coming from target time to target time less delta
    private SortedMap<Float, LogEntity> tree;
    private CompleteLog target;

    public Linking() {
        this.addInTokensType(TokenType.TokenTypeId.ID_IPV4.getId(), new TypeIPv4());
        this.addInTokensType(TokenType.TokenTypeId.ID_IPV6.getId(), new TypeIPv6());
        this.addInTokensType(TokenType.TokenTypeId.ID_STATUS.getId(), new TypeHTTPStatus());
        this.addInTokensType(TokenType.TokenTypeId.ID_EDGERESPONSE.getId(), new TypeEdgeResponse());
    }

    private void addInTokensType(Integer id, TokenType tokenType) {
        tokensType.compute(id, (k,v) -> tokenType);
    }

    public void link(long id, ReportParameter rp) {
        logs.clear();
        try {
            //TODO : if log doest exists
            var log = LogEntity.<LogEntity>findById(id)
                    .onFailure().invoke(x -> logger.log(Level.SEVERE, "Log not found with ID " + id, x))
                    .subscribeAsCompletionStage().get();
            System.out.println(log);
            this.target = new CompleteLog(log,
                    log.getRawLog());

            //Get the target log
            logger.log(Level.INFO,() -> "Log target : " + target + "\n");
            var datetime = log.getDatetime();
            //Get the list of logs within the delta

            var logToLink = LogEntity.<LogEntity>find("datetime between ?1 and ?2",
                            Timestamp.valueOf(datetime.toLocalDateTime().minus(Duration.ofSeconds(rp.delta()))),
                            datetime)
                    .list().subscribe().asCompletionStage().get();
            logs.addAll(logToLink);
            logs.forEach(System.out::println);

            tree = computeProximityTree(target, logs, rp);
        } catch (InterruptedException | ExecutionException e) {
            this.logger.warning("Error while looking for target log." + e);
            Thread.currentThread().interrupt();
        }
    }

    public CompleteLog getTarget() {
        return target;
    }

    public SortedMap<Float, LogEntity> getTree() {
        return tree;
    }

    private void fillHashmap(List<TokenEntity> tokens, HashMap<Integer, List<TokenEntity>> tokensToFill) {
        tokens.forEach(token -> {
            tokensToFill.computeIfAbsent(token.getIdtokentype(),
                    k -> new ArrayList<>());
            tokensToFill.get(token.getIdtokentype()).add(token);
        });
    }

    public SortedMap<Float, LogEntity> computeProximityTree(CompleteLog logTarget, List<LogEntity> logWithinDelta, ReportParameter rp){
        TreeMap<Float, LogEntity> redBlack = new TreeMap<>(Collections.reverseOrder());
        var targetDatetime = logTarget.getDatetime();

        HashMap<Integer, List<TokenEntity>> tokenTarget = new HashMap<>();
        fillHashmap(logTarget.getTokens(), tokenTarget);

        var delta = rp.delta();

        HashMap<Integer, List<TokenEntity>> tokenToLink = new HashMap<>();
        this.tokensType.forEach((id,v) -> tokenToLink.computeIfAbsent(id, k -> new ArrayList<>()));

        System.out.println(delta);
        logWithinDelta.forEach(log -> {
            float proximity = TypeDatetime.computeDateTimeProximity(log.getDatetime().toLocalDateTime(), targetDatetime, delta);
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
        System.out.println(redBlack);
        redBlack.forEach((k,v) -> System.out.println(k + " LOG " + v));
        redBlack.forEach((k,v) -> System.out.println(k));
        return redBlack;
    }

    /**
     * NOT IMPLEMENTED YET
     * Computes proximity between log of id1 and log of id2 for all the tokens in tokenTypes
     * @param id1 target log //A VALIDER
     * @param id2 other log
     * @return A JSON format String containing the proximities for each token
     */
    public String computeLinks(long id1, long id2) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        /*for (TokenType tt : tokenTypes) {
            sb.append(tt.getName()).append(" : ");
        }*/
        sb.append("}");
        return sb.toString();
    }

}
