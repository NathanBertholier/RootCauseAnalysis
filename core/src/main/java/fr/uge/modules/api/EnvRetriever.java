package fr.uge.modules.api;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EnvRetriever {
    @ConfigProperty(name = "REPORT.EXPANDED") boolean DEFAULT_EXPANDED;
    @ConfigProperty(name = "REPORT.NO_CACHE") boolean DEFAULT_CACHE;
    @ConfigProperty(name = "REPORT.DELTA") long DEFAULT_DELTA;
    @ConfigProperty(name = "REPORT.PROXIMITY_LIMIT") int DEFAULT_PROX_LIMIT;
    @ConfigProperty(name = "REPORT.NETWORK_SIZE") int DEFAULT_NET_SIZE;

    public boolean reportDefaultExpanded(){ return DEFAULT_EXPANDED; }
    public boolean reportDefaultCache(){ return DEFAULT_CACHE; }
    public long reportDefaultDelta(){ return DEFAULT_DELTA; }
    public int reportDefaultLimit(){ return DEFAULT_PROX_LIMIT; }
    public int reportDefaultSize(){ return DEFAULT_NET_SIZE; }
}
