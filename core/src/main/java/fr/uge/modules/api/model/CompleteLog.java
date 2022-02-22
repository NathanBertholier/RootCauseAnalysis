package fr.uge.modules.api.model;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import io.smallrye.mutiny.Uni;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public record CompleteLog(long id, Timestamp datetime, String content, List<TokenEntity> tokens) {
    public static Uni<CompleteLog> fromId(long id) {
        return LogEntity
                .<LogEntity>findById(id)
                .map(log -> new CompleteLog(id, log.datetime, log.rawLog.log, log.tokens))
                .onFailure().invoke(error -> System.out.println("Error for id " + id + ": " + error))
                .replaceWithNull();
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
}
