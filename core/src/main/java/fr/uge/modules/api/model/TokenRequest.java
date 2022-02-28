package fr.uge.modules.api.model;

import java.util.List;

public record TokenRequest(String init_datetime, String end_datetime, long id, List<TokenModel> tokens, int rows) {
}
