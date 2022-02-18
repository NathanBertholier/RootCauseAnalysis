package fr.uge.modules.api.model;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompleteLog {
    private final long id;
    private final Timestamp datetime;
    private String content;
    private final ArrayList<TokenEntity> tokens = new ArrayList<>();

    public CompleteLog(long id, Timestamp ldt, List<TokenEntity> tokenSet) {
        this(id, "", ldt, tokenSet);
    }

    public CompleteLog(long id, String content, Timestamp ldt, List<TokenEntity> tokenSet) {
        this.content = content;
        this.id = id;
        this.datetime = ldt;
        tokens.addAll(tokenSet);
    }

    public ArrayList<TokenEntity> getTokens() {
        return tokens;
    }

    public long getId() {
        return id;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Log : ").append(id).append(" {\n");
        sb.append("DateTime : ").append(datetime).append("\n");
        sb.append("Tokens : \n");
        for(TokenEntity token : tokens){
            sb.append("\t").append(token).append("\n");
        }
        sb.append(" }");
        return sb.toString();
    }

    public void setContent(String content) {
        this.content = content;
    }
}
