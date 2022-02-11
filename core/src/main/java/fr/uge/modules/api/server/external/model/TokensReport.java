package fr.uge.modules.api.server.external.model;

public record TokensReport(String token_type, String[] token_value, long count) {
}
