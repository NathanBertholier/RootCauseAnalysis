package fr.uge.modules.api.model.linking;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.serializer.RelationSerializer;

@JsonSerialize(using = RelationSerializer.class)
public record Relation(LogEntity source, LogEntity target, TokensLink tokensLink) {
    @Override
    public String toString() {
        return "Relation{" +
                "source=" + source +
                ", target=" + target +
                ", tokensLink=" + tokensLink +
                '}';
    }
}
