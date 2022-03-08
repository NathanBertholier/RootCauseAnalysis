package fr.uge.modules.api;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EnvRetriever {
    @ConfigProperty(name = "report.default.expanded") boolean DEFAULT_EXPANDED;
    @ConfigProperty(name = "report.default.delta") long DEFAULT_DELTA;
    @ConfigProperty(name = "report.default.proximity_limit") int DEFAULT_PROX_LIMIT;
    @ConfigProperty(name = "report.default.network_size") int DEFAULT_NET_SIZE;

    public boolean reportDefaultExpanded(){ return DEFAULT_EXPANDED; }
    public long reportDefaultDelta(){ return DEFAULT_DELTA; }
    public int reportDefaultLimit(){ return DEFAULT_PROX_LIMIT; }
    public int reportDefaultSize(){ return DEFAULT_NET_SIZE; }
}
