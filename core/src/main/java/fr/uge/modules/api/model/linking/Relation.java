package fr.uge.modules.api.model.linking;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.serializer.RelationSerializer;

import java.util.Objects;

/**
 * Record used to represent a relation between two logs. It contains the two compared logs and a specific object
 * representing every computation used to determine the log proximity, according to a given function.
 * A relation is serialized using a RelationSerializer object.
 */
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
