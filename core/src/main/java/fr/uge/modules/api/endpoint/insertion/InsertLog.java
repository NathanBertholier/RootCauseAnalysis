package fr.uge.modules.api.endpoint.insertion;

import fr.uge.modules.api.model.RawLog;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.LongStream;

/**
 * TODO - Update channels, Unis & Multi returned by processors
 */
@Path("/insertlog")
public class InsertLog {
    private static final Logger logger = Logger.getGlobal();

    @Channel("logs") Emitter<RawLog> emitter;
    private static AtomicLong atomicLong = new AtomicLong();

    @Path("/single")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // TODO Change serializable from JSON to byte[]
    // TODO header or champs in class for ID ?
    public Uni<Long> insertLog(RawLog input) {
        emitter.send(Message.of(input, Metadata.of(new OutgoingRabbitMQMetadata.Builder()
                .withHeader("id", atomicLong.get()).build())));
        return Uni.createFrom().item(atomicLong.getAndIncrement());
    }

    @Path("/batch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Long> insertLog(List<RawLog> inputs) {
        var current = atomicLong.get();
        inputs.forEach(log -> emitter.send(Message.of(log, Metadata.of(new OutgoingRabbitMQMetadata.Builder()
                .withHeader("id", atomicLong.getAndIncrement()).build()))));
        return Multi.createFrom().items(() -> LongStream.range(current, atomicLong.get()).boxed());
    }
}

