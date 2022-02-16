package fr.uge.modules.linking;

import fr.uge.modules.api.model.CompleteLog;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.RawLogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.linking.token.type.TokenType;
import fr.uge.modules.linking.token.type.TypeDatetime;
import fr.uge.modules.linking.token.type.TypeHTTPStatus;
import fr.uge.modules.linking.token.type.TypeIPv4;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;

import java.sql.*;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Linking {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final HashMap<Integer, TokenType> tokensType = new HashMap<>();
    private List<LogEntity> logs = new ArrayList<>(); //Logs coming from target time to target time less delta
    private SortedMap<Float, LogEntity> tree;
    private CompleteLog target;

    public static void main(String[] args) {
        var link = new Linking();
        link.link(58, new ReportParameter(false, 86400, false, 0, 15));
    }

    public Linking() {
        this.addInTokensType(TokenType.TokenTypeId.ID_IPV4.getId(), new TypeIPv4());
        this.addInTokensType(TokenType.TokenTypeId.ID_STATUS.getId(), new TypeHTTPStatus());
    }

    private void addInTokensType(Integer id, TokenType tokenType) {
        tokensType.compute(id, (k,v) -> tokenType);
    }

    public void link(long id, ReportParameter rp) {
        logs.clear();
        try {
            var log = LogEntity.<LogEntity>findById(id).subscribeAsCompletionStage().get();
            this.target = new CompleteLog(log,
                    RawLogEntity.<RawLogEntity>findById(id).subscribeAsCompletionStage().get());
            System.out.println(log);
            System.out.println(target);

            //Get the target log
            logger.log(Level.INFO,() -> "Log target : " + target + "\n");
            var datetime = log.getDatetime();
            //Get the list of logs within the delta
            logs = new ArrayList<>(LogEntity.<LogEntity>find("id != ?1 and datetime between ?2 and ?3", id, datetime, Timestamp.valueOf(datetime.toLocalDateTime().minus(Duration.ofSeconds(rp.delta()))))
                    .list().subscribeAsCompletionStage().get());
            logs.forEach(System.out::println);

            tree = computeProximityTree(target, logs, rp);
        } catch (InterruptedException | ExecutionException e) {
            this.logger.warning("Error while looking for target log." + e);
        }
    }

    public CompleteLog getTarget() {
        return target;
    }

    public SortedMap<Float, LogEntity> getTree() {
        return tree;
    }

    private Uni<CompleteLog> rowToLog(RowIterator<Row> iterator) {
        if(!iterator.hasNext()) return Uni.createFrom().nullItem();
        else {
            var row = iterator.next();
            return Uni.createFrom().item(() -> {
                var idLog = row.getLong("id");
                /*
                var tokens = getTokens(idLog);
                try {
                    return new DatabaseLog(idLog, true, LocalDateTime.now(), tokens.subscribeAsCompletionStage().get());
                } catch (InterruptedException | ExecutionException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                    System.out.println("Error: " + e);
                    return null;
                }
                 */
                return null;
            });
        }
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
        logWithinDelta.forEach(log -> {
            float proximity = TypeDatetime.computeDateTimeProximity(log.getDatetime().toLocalDateTime(), targetDatetime, delta);
            System.out.println("Proximity  : " + proximity);
            fillHashmap(log.getTokens(), tokenToLink);

            proximity += (float) tokenTarget.keySet().stream()
                    .mapToDouble((k) -> tokensType.get(k)
                            .computeProximity(tokenTarget.get(k),
                    tokenToLink.get(k))).sum();

            System.out.println("SUM " + proximity);
            proximity /= tokenTarget.size();// NUMBER OF TOKENS CONSIDERATE

            System.out.println("TOTAL " + proximity);
            if(redBlack.size() > rp.network_size() - 1) {
                if (proximity > redBlack.lastKey()) {
                    redBlack.pollLastEntry();
                    redBlack.put(proximity, log);
                }
            } else {
                redBlack.put(proximity, log);
            }
            tokenToLink.clear();
        });
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
