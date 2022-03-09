package fr.uge.modules.synthetization;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.linking.Relation;

import java.util.List;
import java.util.PriorityQueue;

/**
 * Record containing generated report's information
 * A GeneratedReport object contains the following information:
 * - rootCause, a LogEntity marked as the root cause of a report
 * - relevantLogs, a List of LogEntities representing every log linked to the targeted one
 * - computations, a Queue of every computations between relevant logs and the targeted one, used in an expanded report
 */
public record GeneratedReport(LogEntity rootCause, List<LogEntity> relevantLogs, PriorityQueue<Relation> computations) {
}
