package fr.uge.modules.api.model.linking;

public record Link(long id_source, long id_target, float proximity) {
}
