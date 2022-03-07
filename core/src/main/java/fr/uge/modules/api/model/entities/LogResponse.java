package fr.uge.modules.api.model.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.serializer.LogSerializer;

import java.util.Objects;

@JsonSerialize(using = LogSerializer.class)
public record LogResponse(LogEntity logEntity) {

    public static LogResponse fromEntity(LogEntity logEntity){
        return new LogResponse(Objects.requireNonNull(logEntity));
    }
}
