package fr.uge.modules.api.endpoint.insertion;

import fr.uge.modules.api.model.RawLog;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * TODO - Update channels, Unis & Multi returned by processors
 */
@Path("/insertlog")
public class InsertLog {
    private static final Logger logger = Logger.getGlobal();
    @Channel("logs") Emitter<RawLog> emitter;

    @Path("/single")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // TODO Change serializable from JSON to byte[]
    // TODO header or champs in class for ID ?
    public Uni<Response> insertLog(RawLog input) {
        return Panache.withTransaction(input::persist)
                .map(item -> Response
                        .created(URI.create("/insertlog/single/"))
                        .entity(item)
                        .build()
                );
    }

    @Path("/batch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Ne fonctionne pas pour le moment, simple test
    public Uni<Long> insertLog(List<RawLog> inputs) {
        inputs.forEach(this::insertLog);
        return RawLog.count();
    }
}

