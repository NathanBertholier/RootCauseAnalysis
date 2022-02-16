package fr.uge.modules.api.model;

import fr.uge.modules.api.model.entities.Token;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompleteLog {
    private final long id;
    private final LocalDateTime datetime;
    private String content;
    private final ArrayList<Token> tokens = new ArrayList<>();

    public CompleteLog(long id, LocalDateTime ldt, List<Token> tokenSet) {
        this(id, "", ldt, tokenSet);
    }

    public CompleteLog(long id, String content, LocalDateTime ldt, List<Token> tokenSet) {
        this.content = content;
        this.id = id;
        this.datetime = ldt;
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

    public String getContent() {
        return content;
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
        sb.append(" }");
        return sb.toString();
    }

    public void setContent(String content) {
        this.content = content;
    }
}
