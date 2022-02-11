package fr.uge.modules.linking.synthetization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.uge.db.insert.monitoring.MonitorInserter;
import fr.uge.modules.api.server.external.model.Log;
import fr.uge.modules.api.server.external.model.ReportResponse;
import fr.uge.modules.api.server.external.model.TokensReport;
import fr.uge.modules.data.log.DatabaseLog;
import fr.uge.modules.api.server.external.model.ReportParameter;
import fr.uge.modules.data.token.Token;

import java.io.IOException;
import java.sql.SQLException;
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

    public static ReportResponse getReport(long rootlog, ReportParameter reportParameter) throws SQLException {
        Linking l = new Linking("jdbc:postgresql://" +
                PROPERTIES.getProperty("DBSRV") +
                ":5432/" +
                PROPERTIES.getProperty("DB") +
                "?user=" +
                PROPERTIES.getProperty("DBLOGIN") +
                "&password=" +
                PROPERTIES.getProperty("DBPWD") +
        "&stringtype=unspecified",rootlog, reportParameter);
        var rootLog = l.getTarget();
        var map = l.getTree();
        return new ReportResponse(new fr.uge.modules.api.server.external.model.Log(rootLog.getId(),rootLog.getBody(),rootLog.getDatetime().toString()),getTokens(map),getLogs(map));
    }

    private static TokensReport[] getTokens(SortedMap<Float, DatabaseLog> map) {
        ArrayList<Token> list = new ArrayList<>();
        ArrayList<TokensReport> tokens = new ArrayList<>();
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
            tokens.add(new TokensReport(k,tabstring,entry2.getKey()));
        });
        return tokens.toArray(TokensReport[]::new);
    }

    private static ArrayNode getProximity(SortedMap<Float, DatabaseLog> map) {
        ArrayNode prox = mapper.createArrayNode();
        map.forEach((k, v) -> {
            ObjectNode log = mapper.createObjectNode();
            log.put("id", v.getId());
            log.put("proximity", k);
            prox.add(log);
        });
        return prox;
    }

    private static fr.uge.modules.api.server.external.model.Log[] getLogs(SortedMap<Float, DatabaseLog> map) {
        ArrayList<fr.uge.modules.api.server.external.model.Log> logs = new ArrayList<>();
        map.forEach((k, v) -> {
            logs.add(new fr.uge.modules.api.server.external.model.Log(v.getId(),v.getBody(),v.getDatetime().toString()));
        });
        return logs.toArray(Log[]::new);
    }

    public static void main(String[] args) throws SQLException {
        int delta = 86400;
        int id_logtarget = 8;
        ReportParameter rp = new ReportParameter(true, 1L, true, 1F, 1);
        System.out.println(Synthetization.getReport(id_logtarget, rp));
    }
}
