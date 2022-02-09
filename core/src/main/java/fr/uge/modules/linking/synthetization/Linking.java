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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Linking {

    private static final String TYPE_DATE = "datetime";
    private static final String TYPE_IPV4 = "IPv4";
    private static final String IDTOKENTYPE = "idtokentype";
    private static final String VALUE = "value";

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final Connection connection;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Log target;
    private List<TokenType> tokenTypes = new ArrayList<>(); //Token Types to considerate
    private List<Log> logs; //Logs coming from target time to target time less delta
    private SortedMap<Float,Log> tree;

    public Linking(String connUrl, long id, ReportParameter rp) throws SQLException {
        initTokenTypes();
        this.connection = DriverManager.getConnection(connUrl);
        //Get the target log
        target = fetchLog(id);
        logger.log(Level.INFO,() -> "Log target : " + target + "\n") ;
        target.setBody(fetchRawLog(id));

        //Get the list of logs within the delta
        logs = fetchListLog(id, target.getDatetime(), rp.getDelta());
        tree = computeProximtyTree(target, logs, rp);
    }
    public Log getTarget(){return target;}
    public SortedMap<Float, Log> getTree() {
        return tree;
    }

    private void initTokenTypes() {
        tokenTypes.add(new TypeDatetime());
        tokenTypes.add(new TypeIPv4());
    }

    private Token createToken(String type, String value) {
        TokenType tt;
        switch(type){
            case TYPE_DATE -> {tt = new TypeDatetime();}
            case TYPE_IPV4 -> {tt = new TypeIPv4();}
            default -> throw new IllegalArgumentException();
        }
        return new Token(value, tt);
    }

    /**
     * Fetches the target log in database with its infos
     * @param idlogtarget id of the target log
     * @return the target log with his timestamp and other tokens
     * @throws SQLException
     */
    private Log fetchLog(long idlogtarget) throws SQLException {
        //Query to fetch the tokens of the logtarget in database
        try(PreparedStatement fetchLog = connection.prepareStatement("SELECT * FROM log INNER JOIN token t ON log.id = t.idlog WHERE log.id = ?")){
            fetchLog.setLong(1, idlogtarget);
            ResultSet logTarget = fetchLog.executeQuery();

            ArrayList<Token> tokenSet = new ArrayList<>();
            logTarget.next();
            //fetching data in the resultset given from query
            LocalDateTime ldt = logTarget.getTimestamp(TYPE_DATE).toLocalDateTime();
            tokenSet.add(createToken(fetchTokenType(logTarget.getInt(IDTOKENTYPE)), logTarget.getString(VALUE)));

            //while the resultset contains other lignes, fetching data for each type of token inside
            while(logTarget.next()) {
                tokenSet.add(createToken(fetchTokenType(logTarget.getInt(IDTOKENTYPE)), logTarget.getString(VALUE)));
            }
            return new Log(idlogtarget, true, ldt, tokenSet);
        }
    }

    /**
     * Fetches all the logs within the delta given in parameter different from the log target
     * @param idtarget id of the target log
     * @param datetime datetime of the target log
     * @param delta delta of time to search
     * @return the lists of logs within the delta
     * @throws SQLException
     */
    private List<Log> fetchListLog(long idtarget, LocalDateTime datetime, int delta) throws SQLException {
        LocalDateTime ldtminusdelta = datetime.minus(Duration.ofSeconds(delta));
        try(PreparedStatement fetchListLog = connection.prepareStatement("SELECT * FROM log INNER JOIN token t ON log.id = t.idlog WHERE datetime BETWEEN ? AND ? AND log.id != ?")) {
            fetchListLog.setTimestamp(1, Timestamp.valueOf(ldtminusdelta.format(formatter)));
            fetchListLog.setTimestamp(2, Timestamp.valueOf(datetime.format(formatter)));
            fetchListLog.setLong(3, idtarget);
            ResultSet lines = fetchListLog.executeQuery();
            long id;
            LocalDateTime ldt;
            List<Log> lst = new ArrayList<>();
            List<Token> tokenSet = new ArrayList<>();
            Log current;
            while (lines.next()) {
                //init log
                id = lines.getLong("id");
                ldt = lines.getTimestamp(TYPE_DATE).toLocalDateTime();
                //get tokens
                tokenSet.add(createToken(fetchTokenType(lines.getInt(IDTOKENTYPE)), lines.getString(VALUE)));
                for (int i = 1; i < tokenTypes.size() - 1; i++) {
                    lines.next();
                    tokenSet.add(createToken(fetchTokenType(lines.getInt(IDTOKENTYPE)), lines.getString(VALUE)));
                }
                current = new Log(id, false, ldt, tokenSet);
                current.setBody(fetchRawLog(id));
                lst.add(current);
                tokenSet.clear();
            }
            return lst;
        }
    }

    public String fetchTokenType(int id) throws SQLException {
        String res;
        try( PreparedStatement fetchTokenType = connection.prepareStatement("SELECT name FROM tokentype INNER JOIN token t ON tokentype.id = t.id WHERE tokentype.id = ?")) {
            fetchTokenType.setLong(1, id);
            ResultSet tokenType = fetchTokenType.executeQuery();
            tokenType.next();

            res = tokenType.getString("name");
            return res;
        }
    }

    public String fetchRawLog(long id) throws SQLException {
        String res;
        try( PreparedStatement fetchTokenType = connection.prepareStatement("SELECT value FROM rawlog WHERE id = ?")) {
            fetchTokenType.setLong(1, id);
            ResultSet tokenType = fetchTokenType.executeQuery();
            tokenType.next();
            res = tokenType.getString(VALUE);
            return res;
        }
    }

    public SortedMap<Float, Log> computeProximtyTree(Log target, List<Log> logWithinDelta, ReportParameter rp){
        TreeMap<Float, Log> redBlack = new TreeMap<>(Collections.reverseOrder());
        logWithinDelta.forEach(log -> {
            float proximity = 0;
            float tmp = TypeDatetime.computeDateTimeProximity(log.getDatetime(), target.getDatetime(), rp.getDelta());
            proximity += tmp;
            for(int i = 0; i < tokenTypes.size() - 1; i++){
                tmp = log.getTokens().get(i).getType().computeProximity(log.getTokens().get(i), target.getTokens().get(i));
                proximity += tmp;
            }
            proximity /= tokenTypes.size();
            if(redBlack.size() > rp.getNetworkSize() - 1) {
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
     * NOT IMPLEMENTED YET
     * Computes proximity between log of id1 and log of id2 for all the tokens in tokenTypes
     * @param id1 target log //A VALIDER
     * @param id2 other log
     * @return A JSON format String containing the proximities for each token
     */
    public String computeLinks(long id1, long id2) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (TokenType tt : tokenTypes) {
            sb.append(tt.getName()).append(" : ");
        }
        sb.append("}");
        return sb.toString();
    }

}
