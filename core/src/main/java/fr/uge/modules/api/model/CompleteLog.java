package fr.uge.modules.api.model;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record CompleteLog(long id, Timestamp datetime, String content, List<TokenEntity> tokens) {
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
}
