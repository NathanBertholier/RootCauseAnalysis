package fr.uge.modules.synthetization;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.linking.Relation;

import java.util.List;
import java.util.TreeSet;

public record GeneratedReport(LogEntity rootCause, List<LogEntity> relevantLogs, TreeSet<Relation> computations) {
}
