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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * TODO - Update channels, Unis & Multi returned by processors - Waiting for flo's task
 */
@Path("/insertlog")
public class InsertLog {
    @Channel("log-requests") Emitter<Rawlog> emitter;
    private static AtomicInteger atomicInteger = new AtomicInteger();

    @Path("/single")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Integer> insertLog(Rawlog input) {
        emitter.send(input);
        return Uni.createFrom().item(atomicInteger.getAndIncrement());
    }

    @Path("/batch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Integer> insertLog(List<Rawlog> inputs) {
        var current = atomicInteger.get();
        System.out.println("Inputs: " + inputs);
        inputs.forEach(log -> {
            emitter.send(log);
            atomicInteger.getAndIncrement();
        });
        return Multi.createFrom().items(() -> IntStream.range(current, atomicInteger.get()).boxed());
    }
}

