package fr.uge.modules.api.model.linking;

import fr.uge.modules.api.model.entities.LogEntity;

import java.util.List;
import java.util.Map;

public record Relation(LogEntity source, LogEntity target, TokensLink tokensLinks) {
    @Override
    public String toString() {
        return "Relation{" +
                "source=" + source +
                ", target=" + target +
                ", tokensLinks=" + tokensLinks +
                '}';
    }
}
