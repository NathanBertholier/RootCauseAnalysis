package fr.uge.modules.api.model;

import fr.uge.modules.api.model.entities.LogEntity;

import java.util.List;

public record TokensResponse(List<LogEntity> logDemonstrators) {
}
