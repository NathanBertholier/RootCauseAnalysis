package fr.uge.modules.api.server.external.insertion;

import fr.uge.modules.api.server.external.model.Rawlog;
import io.smallrye.common.annotation.NonBlocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RawlogProcessor {
    @Incoming(value = "logs")
    //@Outgoing(value = "logs-ids")
    @NonBlocking
    public void process(Rawlog input){
        System.out.println("Processing log " + input.toString());
    }
}

