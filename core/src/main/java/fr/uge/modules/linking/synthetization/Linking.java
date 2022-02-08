package fr.uge.modules.linking.synthetization;

import fr.uge.modules.data.log.Log;
import fr.uge.modules.data.report.ReportParameter;
import fr.uge.modules.data.token.Token;
import fr.uge.modules.data.token.type.*;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Linking {

    private final Connection connection;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Log target;
    private List<TokenType> tokenTypes = new ArrayList<>(); //Token Types to considerate
    private List<Log> logs; //Logs coming from target time to target time less delta
    private TreeMap<Float,Log> tree;

    public Linking(String connUrl, long id, ReportParameter rp) throws SQLException {
        initTokenTypes();
        this.connection = DriverManager.getConnection(connUrl);
        System.out.println("hi");
        //Get the target log
        target = fetchLog(connection, id);
        System.out.println(target);

        //Get the list of logs within the delta
        logs = fetchListLog(connection, id, target.getDatetime(), rp.getDelta());
        TreeMap<Float, Log> map = computeProximtyTree(target, logs, rp);
        //System.out.println(map.toString());
    }

    public TreeMap<Float, Log> getTree() {
        return tree;
    }

    private void initTokenTypes() {
        tokenTypes.add(new TypeDatetime());
        tokenTypes.add(new TypeIPv4());
    }

    private Token createToken(String type, String value) {
        TokenType tt;
        //System.out.println("into create token : " + type);
        switch(type){
            case "datetime" -> tt = new TypeDatetime();
            case "IPv4" -> tt = new TypeIPv4();
            default -> throw new IllegalArgumentException();
        }
        return new Token(value, tt);
    }

    /**
     * Fetches the target log in database with its infos
     * @param conn Connection to database
     * @param idlogtarget id of the target log
     * @return the target log with his timestamp and other tokens
     * @throws SQLException
     */
    private Log fetchLog(Connection conn, long idlogtarget) throws SQLException {
        //Query to fetch the tokens of the logtarget in database
        PreparedStatement fetchLog = conn.prepareStatement("SELECT * FROM log INNER JOIN token t ON log.id = t.idlog WHERE log.id = ?");
        //param 1 for the index
        fetchLog.setLong(1, idlogtarget);
        ResultSet logTarget = fetchLog.executeQuery();
        ArrayList<Token> tokenSet = new ArrayList<>();
        logTarget.next();
        //fetching data in the the resultset given from query
        LocalDateTime ldt = logTarget.getTimestamp("datetime").toLocalDateTime();
        tokenSet.add(createToken(logTarget.getString("type"), logTarget.getString("value")));
        //while the resultset contains other lignes, fetching data for each type of token inside
        while(logTarget.next()) {
            tokenSet.add(createToken(logTarget.getString("type"), logTarget.getString("value")));
        }
        return new Log(idlogtarget, true, ldt, tokenSet);
    }

    /**
     * Fetches all the logs within the delta given in parameter different from the log target
     * @param conn to database
     * @param idtarget id of the target log
     * @param datetime datetime of the target log
     * @param delta delta of time to search
     * @return the lists of logs within the delta
     * @throws SQLException
     */
    private List<Log> fetchListLog(Connection conn, long idtarget, LocalDateTime datetime, int delta) throws SQLException {
        LocalDateTime ldtminusdelta = datetime.minus(Duration.ofSeconds(delta));
        //System.out.println(ldt.toString() + " minus delta : " +  ldtminusdelta);
        //System.out.println(Timestamp.valueOf(ldtminusdelta.format(formatter)));
        PreparedStatement fetchListLog = connection.prepareStatement("SELECT * FROM log INNER JOIN token t ON log.id = t.idlog WHERE datetime BETWEEN ? AND ? AND log.id != ?");
        fetchListLog.setTimestamp(1,Timestamp.valueOf(ldtminusdelta.format(formatter)));
        fetchListLog.setTimestamp(2,Timestamp.valueOf(datetime.format(formatter)));
        fetchListLog.setLong(3, idtarget);
        ResultSet lines = fetchListLog.executeQuery();
        long id;
        LocalDateTime ldt;
        List<Log> lst = new ArrayList<>();
        List<Token> tokenSet = new ArrayList<>();
        while(lines.next()) {
            //init log
            id = lines.getLong("id");
            ldt = lines.getTimestamp("datetime").toLocalDateTime();
            //get tokens
            tokenSet.add(createToken(lines.getString("type"), lines.getString("value")));
            lines.next();
            tokenSet.add(createToken(lines.getString("type"), lines.getString("value")));
            lst.add(new Log(id, false, ldt, tokenSet));
            tokenSet.clear();
        }
        return lst;
    }

    public TreeMap<Float, Log> computeProximtyTree(Log target, List<Log> logWithinDelta, ReportParameter rp){
        TreeMap<Float, Log> redBlack = new TreeMap<Float, Log>(Collections.reverseOrder());
        logWithinDelta.forEach(log -> {
            float proximity = 0;
            for(int i = 0; i < tokenTypes.size(); i++){
                if(log.getTokens().get(i).getType().getName() == "datetime"){
                    float tmp = TypeDatetime.computeDateTimeProximity(log.getDatetime(), target.getDatetime(), rp.getDelta());
                    proximity += tmp;
                } else {
                    float tmp = log.getTokens().get(i).getType().computeProximity(log.getTokens().get(i), target.getTokens().get(i));
                    proximity += tmp;
                }
            }
            proximity /= tokenTypes.size();
            if(redBlack.size() > rp.getNetworkSize()) {
                if (proximity > redBlack.lastKey()) {
                    redBlack.pollLastEntry();
                    redBlack.put(proximity, log);
                }
            } else {
                redBlack.put(proximity, log);
            }
        });
        return redBlack;
    }

    /**
     * Computes proximity between log of id1 and log of id2 for all the tokens in tokenTypes
     * @param id1 target log //A VALIDER
     * @param id2 other log
     * @return A JSON format String containing the proximities for each token
     */
    public String computeLinks(long id1, long id2) {
        StringBuilder sb = new StringBuilder();
        int size = tokenTypes.size();
        sb.append("{");
        for (TokenType tt : tokenTypes) {
            sb.append(tt.getName()).append(" : ");
        }
        sb.append("}");
        return sb.toString();
    }

    public static void main(String[] args) throws SQLException {

        int delta = 86400;
        int id_logtarget = 8;
        ReportParameter rp = new ReportParameter(delta, 5);
        Linking l = new Linking("jdbc:postgresql://localhost:5432/rootcause?user=root&password=root&stringtype=unspecified", id_logtarget, rp);
        System.out.println(l.getTree().toString());

    }

}
