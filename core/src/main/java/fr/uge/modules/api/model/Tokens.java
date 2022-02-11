package fr.uge.modules.api.model;

import java.sql.Timestamp;
import java.util.List;

public record Tokens(long id, Timestamp timestamp, List<TokenModel> tokens) {
}
