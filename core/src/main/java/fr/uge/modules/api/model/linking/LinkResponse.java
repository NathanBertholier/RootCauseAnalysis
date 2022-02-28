package fr.uge.modules.api.model.linking;

import fr.uge.modules.api.model.entities.LogEntity;

public record LinkResponse(LogEntity source, LogEntity target, Link link) {
    @Override
    public String toString() {
        return "LinkResponse{" +
                "source=" + source +
                ", target=" + target +
                ", link=" + link +
                '}';
    }
}
