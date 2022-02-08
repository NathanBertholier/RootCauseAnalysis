package fr.uge.modules.linking.synthetization;

import fr.uge.modules.data.log.Log;
import fr.uge.modules.data.report.ReportParameter;
import fr.uge.modules.data.token.Token;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


public class Synthetization {
    private ReportParameter params;
    private int idroot;
    public Synthetization(int rootlog, ReportParameter reportParameter){
        idroot = rootlog;
        params= reportParameter;
    }

    public JSONObject getReport() throws SQLException {
        Linking l = new Linking("jdbc:postgresql://localhost:5432/rootcause?user=root&password=root&stringtype=unspecified", idroot, params);
        var rootLog = l.getTarget();
        var map = l.getTree();
        JSONObject report = new JSONObject();
        JSONObject root = new JSONObject();
        JSONArray tokens = getTokens(map);
        JSONArray logs = getLogs(map);
        JSONArray proximity = getProximity(map);
        root.put("id",rootLog.getId());
        root.put("content",rootLog.getBody());
        root.put("datetime",rootLog.getDatetime());

        report.put("tokens",tokens);

        report.put("logs",logs);
        report.put("proximities",proximity);
        report.put("root",root);
        return report;
    }
    private JSONArray getTokens(SortedMap<Float,Log> map){
        ArrayList<Token> list = new ArrayList<>();
        JSONArray tokens = new JSONArray();
        map.forEach((k,v)->{
            list.addAll(v.getTokens());
        });
        var groupByType =  list.stream().
                collect(Collectors.groupingBy(t->t.getType().getName()));
        var numberByToken =groupByType.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e ->
                e.getValue().stream().collect(Collectors.groupingBy(Token::getValue,
                        Collectors.counting())).entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue))));
        numberByToken.forEach((k,v)->{
            JSONObject node = new JSONObject();
            node.put("name",k);
            Map.Entry<String,Long> entry = v.entrySet().iterator().next();
            Map<Long,List<String>> value = new HashMap<>();
            v.forEach((k2,v2)->{
                if(value.containsKey(v2)){
                    value.get(v2).add(k2);
                }else{
                    var x =  new ArrayList<String>();
                    x.add(k2);
                    value.put(v2,x);
                }
            });
            var sortedlist= value.entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByKey())).collect(Collectors.toMap(Map.Entry::getKey,
                            Map.Entry::getValue,(oldValue, newValue) -> oldValue, LinkedHashMap::new));
            Map.Entry<Long,List<String>> entry2 = sortedlist.entrySet().iterator().next();
            node.put("value",entry2.getValue());
            node.put("count",entry2.getKey());
            tokens.put(node);
        });
        return tokens;
    }
    private JSONArray getProximity(SortedMap<Float,Log> map){
        JSONArray prox = new JSONArray();
        map.forEach((k,v)->{
            JSONObject log = new JSONObject();
            log.put("id",v.getId());
            log.put("proximity",k);
            prox.put(log);
        });
        return prox;
    }
    private JSONArray getLogs(SortedMap<Float,Log> map){
        JSONArray logs = new JSONArray();
        map.forEach((k,v)->{
            JSONObject log = new JSONObject();
            log.put("id",v.getId());
            log.put("content",v.getBody());
            log.put("datetime",v.getDatetime());
            logs.put(log);
        });
        return logs;
    }

    public static void main(String[] args) throws SQLException {
        int delta = 86400;
        int id_logtarget = 8;
        ReportParameter rp = new ReportParameter(delta, 5);
        var synth = new Synthetization(id_logtarget,rp);
        System.out.println(synth.getReport().toString());
    }
}
