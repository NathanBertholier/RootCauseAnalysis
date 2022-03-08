package fr.uge.modules.synthetization;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.linking.Relation;

import java.util.List;
import java.util.PriorityQueue;

public record GeneratedReport(LogEntity rootCause, List<LogEntity> relevantLogs, PriorityQueue<Relation> computations) {
}
