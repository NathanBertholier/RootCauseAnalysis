package fr.uge.modules.api.model.report;

import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.entities.LogEntity;

import java.util.List;
import java.util.Set;

/**
 * Interface representing every kind of generated report, used to retrieve report's information no matter which
 * class implements it.
 */
public interface GenericReport {
    LogEntity getRoot();
    LogEntity getTarget();
    Set<TokensMostSeen> getSeenTokens();
    List<LogEntity> getRelevantLogs();
}
