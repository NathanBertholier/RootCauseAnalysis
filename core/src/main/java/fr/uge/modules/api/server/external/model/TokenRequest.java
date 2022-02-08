package fr.uge.modules.api.server.external.model;

public record TokenRequest(String init_datetime, String end_datetime, long id, TokenModel tokenModel, int rows) {
}
