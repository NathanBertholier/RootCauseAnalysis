package fr.uge.modules.synthetization;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.linking.LinkResponse;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public record GeneratedReport(LogEntity rootCause, Set<LinkResponse> computations) {
}
