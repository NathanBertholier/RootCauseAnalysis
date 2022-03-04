package fr.uge.modules.api.model.linking;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.serializer.RelationSerializer;

import java.util.Objects;

@JsonSerialize(using = RelationSerializer.class)
public record Relation(LogEntity source, LogEntity target, TokensLink tokensLink) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relation relation = (Relation) o;
        return source.equals(relation.source) && target.equals(relation.target) && tokensLink.equals(relation.tokensLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, tokensLink);
    }

    @Override
    public String toString() {
        return "Relation{" +
                "source=" + source +
                ", target=" + target +
                ", tokensLink=" + tokensLink +
                '}';
    }
}
