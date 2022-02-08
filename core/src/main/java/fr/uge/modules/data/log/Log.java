package fr.uge.modules.data.log;


import fr.uge.modules.data.token.Token;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Log {
    private final long id;
    private final LocalDateTime datetime;
    private final ArrayList<Token> tokens = new ArrayList<>();
    private String body;
    private boolean target = false;

    public Log(long id, boolean isTarget, LocalDateTime ldt, List<Token> tokenSet) {
        this.id = id;
        this.datetime = ldt;
        this.target = isTarget;
        tokens.addAll(tokenSet);
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Log : ").append(id).append(" {\n");
        sb.append("DateTime : ").append(datetime).append("\n");
        sb.append("Tokens : \n");
        for(Token token : tokens){
            sb.append("\t").append(token).append("\n");
        }
        sb.append("\nisTarget : ").append(target).append(" }");
        return sb.toString();
    }
}
