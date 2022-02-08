package fr.uge.modules.api.server.external.insertion;

import fr.uge.modules.api.server.external.model.Rawlog;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.LongStream;

/**
 * TODO - Update channels, Unis & Multi returned by processors - Waiting for flo's task
 */
@Path("/insertlog")
public class InsertLog {
    @Channel("log-requests") Emitter<Rawlog> emitter;
    private static final AtomicLong atomicLong = new AtomicLong();
    private static final Logger LOGGER = Logger.getLogger(InsertLog.class.getName());

    @Path("/single")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Long> insertLog(Rawlog input) {
        LOGGER.log(Level.INFO, "Received rawlogs: " + input);
        emitter.send(input);
        return Uni.createFrom().item(atomicLong.getAndIncrement());
    }

    @Path("/batch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Long> insertLog(List<Rawlog> inputs) {
        var current = atomicLong.get();
        LOGGER.log(Level.INFO, "Received rawlogs: " + inputs);
        inputs.forEach(log -> {
            emitter.send(log);
            atomicLong.getAndIncrement();
        });
        return Multi.createFrom().items(() -> LongStream.range(current, atomicLong.get()).boxed());
    }
}

