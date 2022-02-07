package fr.uge.modules.api.server.external.insertion;

import fr.uge.modules.api.server.external.model.Log;
import io.smallrye.common.annotation.NonBlocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RawlogProcessor {
    @Incoming(value = "logs-requests")
    //@Outgoing(value = "logs-ids")

    @NonBlocking
    public void process(Log input){
        System.out.println("Processing log " + input.toString());
    }
}

