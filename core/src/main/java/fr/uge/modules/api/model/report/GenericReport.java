package fr.uge.modules.api.model.report;

import fr.uge.modules.api.model.TokensMostSeen;
import fr.uge.modules.api.model.entities.LogEntity;

import java.util.List;
import java.util.Set;

public interface GenericReport {
    LogEntity getRoot();
    LogEntity getTarget();
    Set<TokensMostSeen> getSeenTokens();
    List<LogEntity> getRelevantLogs();
}
