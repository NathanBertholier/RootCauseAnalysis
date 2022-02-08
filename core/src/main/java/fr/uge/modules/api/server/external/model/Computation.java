package fr.uge.modules.api.server.external.model;

public record Computation(String token_type, String value_log_first, String value_log_second, float proximity) {
}
