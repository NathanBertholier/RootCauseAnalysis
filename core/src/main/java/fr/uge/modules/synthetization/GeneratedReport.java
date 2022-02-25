package fr.uge.modules.synthetization;

import fr.uge.modules.api.model.entities.LogEntity;

import java.util.SortedMap;

public record GeneratedReport(LogEntity rootCause, SortedMap<Double, LogEntity> entities) {
}
