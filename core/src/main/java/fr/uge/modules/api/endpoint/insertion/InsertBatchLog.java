package fr.uge.modules.api.endpoint.insertion;

import fr.uge.modules.api.model.entities.RawLogEntity;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.inject.Inject;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.LongStream;

@Path("/insert/batch")
public class InsertBatchLog {
    private static final Function<Object, Response> withCreated = entity -> Response.created(URI.create("/insert/batch")).entity(entity).build();
    private static final Supplier<Response> withServerError = () -> Response.serverError().build();
    private static final Function<List<RawLogEntity>, LongStream> asLongStream = inputs -> LongStream.of(inputs.stream().mapToLong(r -> r.id).toArray());

    private static final Logger logger = Logger.getGlobal();
    @Channel("logs") Emitter<RawLogEntity> emitter;
    @Inject Validator validator;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> insertLog(List<RawLogEntity> inputs) {
        var rawLogs = inputs.stream().filter(rawlog -> validator.validate(rawlog).isEmpty()).toList();
        if(rawLogs.isEmpty()) return Uni.createFrom().item(Response.status(400).entity("Log content cannot be empty").build());

        return Panache.withTransaction(
                () -> RawLogEntity.persist(rawLogs)
                        .onItemOrFailure().transform((success, error) -> {
                            if(error != null) {
                                logger.severe("Errror while inserting: " + error);
                                return withServerError.get();
                            } else {
                                logger.info("Inserted: " + rawLogs);
                                rawLogs.forEach(rawLogEntity -> emitter.send(rawLogEntity));
                                return withCreated.apply(asLongStream.apply(rawLogs));
                            }
                        })
        );
    }
}
