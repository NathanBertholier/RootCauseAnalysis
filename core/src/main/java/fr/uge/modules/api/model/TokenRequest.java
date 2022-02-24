package fr.uge.modules.api.model;

import fr.uge.modules.api.model.entities.TokenEntity;

public record TokenRequest(String init_datetime, String end_datetime, long id, TokenEntity token, int rows) {
}
