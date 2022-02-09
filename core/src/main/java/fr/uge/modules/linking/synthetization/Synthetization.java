package fr.uge.modules.linking.synthetization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.msgsimple.provider.MessageSourceLoader;
import fr.uge.db.insert.logtoken.LogInserter;
import fr.uge.modules.data.log.Log;
import fr.uge.modules.data.report.ReportParameter;
import fr.uge.modules.data.token.Token;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class Synthetization {
    private static final Properties PROPERTIES = new Properties();
    private static final Logger LOGGER = Logger.getGlobal();
    private ReportParameter params;
    private int idroot;
    private ObjectMapper mapper = new ObjectMapper();


    public Synthetization(int rootlog, ReportParameter reportParameter) {
        idroot = rootlog;
        params = reportParameter;
    }

    public ObjectNode getReport() throws SQLException {
        try {
            PROPERTIES.load(LogInserter.class.getClassLoader().getResourceAsStream("init.properties"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException", e);
        }
        Linking l = new Linking("jdbc:postgresql://" +
                PROPERTIES.getProperty("DBSRV") +
                ":5432/" +
                PROPERTIES.getProperty("DB") +
                "?user=" +
                PROPERTIES.getProperty("DBLOGIN") +
                "&password=" +
                PROPERTIES.getProperty("DBPWD") +
                "&stringtype=unspecified", idroot, params);
        var rootLog = l.getTarget();
        var map = l.getTree();
        ObjectNode report = mapper.createObjectNode();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode tokens = getTokens(map);
        ArrayNode logs = getLogs(map);
        ArrayNode proximity = getProximity(map);
        root.put("id", rootLog.getId());
        root.put("content", rootLog.getBody());
        root.put("datetime", rootLog.getDatetime().toString());
        report.set("root", root);
        report.set("tokens", tokens);

        report.set("logs", logs);
        report.set("proximities", proximity);

        return report;
    }

    private ArrayNode getTokens(SortedMap<Float, Log> map) {
        ArrayList<Token> list = new ArrayList<>();
        ArrayNode tokens = mapper.createArrayNode();
        map.forEach((k, v) -> {
            list.addAll(v.getTokens());
        });
        var groupByType = list.stream().
                collect(Collectors.groupingBy(t -> t.getType().getName()));
        var numberByToken = groupByType.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e ->
                        e.getValue().stream().collect(Collectors.groupingBy(Token::getValue,
                                        Collectors.counting())).entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
        numberByToken.forEach((k, v) -> {
            ObjectNode node = mapper.createObjectNode();
            node.put("name", k);
            Map.Entry<String, Long> entry = v.entrySet().iterator().next();
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
            node.put("value", entry2.getValue().toString());
            node.put("count", entry2.getKey());
            tokens.add(node);
        });
        return tokens;
    }

    private ArrayNode getProximity(SortedMap<Float, Log> map) {
        ArrayNode prox = mapper.createArrayNode();
        map.forEach((k, v) -> {
            ObjectNode log = mapper.createObjectNode();
            log.put("id", v.getId());
            log.put("proximity", k);
            prox.add(log);
        });
        return prox;
    }

    private ArrayNode getLogs(SortedMap<Float, Log> map) {
        ArrayNode logs = mapper.createArrayNode();
        map.forEach((k, v) -> {
            ObjectNode log = mapper.createObjectNode();
            log.put("id", v.getId());
            log.put("content", v.getBody());
            log.put("datetime", v.getDatetime().toString());
            logs.add(log);
        });
        return logs;
    }

    public static void main(String[] args) throws SQLException {
        int delta = 86400;
        int id_logtarget = 8;
        ReportParameter rp = new ReportParameter(delta, 5);
        var synth = new Synthetization(id_logtarget, rp);
        synth.getReport();
    }
}
