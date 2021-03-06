package fr.uge.modules.api.endpoint.insertion;

import fr.uge.modules.api.model.entities.RawLogEntity;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.LongStream;


/**
 * Endpoint used to insert logs inside the database. This endpoint accepts one or many logs, contained in an array.
 * Performs batch in order to increase insertion speediness. Returns an array that contains ids of inserted logs.
 * Returns an error if one of the submitted logs does not respect standard format.
 * Insertions respect ACID constraints.
 */
@Path("/insertlog")
public class InsertBatchLog {
    private static final Function<Object, Response> withCreated = entity -> Response.created(URI.create("/insert/batch")).entity(entity).build();
    private static final Function<Throwable, Response> withServerError = error -> Response.serverError().entity(error.getMessage()).build();
    private static final Function<List<RawLogEntity>, LongStream> asLongStream = inputs -> LongStream.of(inputs.stream().mapToLong(r -> r.id).toArray());

    private static final Logger LOGGER = Logger.getLogger(InsertBatchLog.class.getName());

    // Queue to tokenizer module
    @Channel("token-out")
    @OnOverflow(OnOverflow.Strategy.UNBOUNDED_BUFFER)
    Emitter<RawLogEntity> emitter;

    @Inject
    Validator rawLogValidator;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> insertLog(@NotNull List<RawLogEntity> inputs) {
        var violations = inputs.stream()
                .map(rawLogValidator::validate)
                .filter(Predicate.not(Set::isEmpty))
                .flatMap(s -> s.stream().distinct())
                .map(ConstraintViolation::getMessage)
                .toList();

        // Constraints validator. If one of the submitted logs does not respect standard format, returns an error.
        if (!violations.isEmpty()) {
            return Uni.createFrom().item(Response.status(400).entity(violations).build());
        }

        // Otherwise, performs transaction in order to insert data in DB.
        return Panache.withTransaction(
                () -> PanacheEntityBase.persist(inputs)
                        .onItemOrFailure().transform((success, error) -> {
                            if (error != null) {
                                LOGGER.severe(() -> "Error while inserting: " + error);
                                return withServerError.apply(error);
                            } else {
                                LOGGER.info(() -> "Inserted: " + inputs);
                                inputs.forEach(emitter::send);
                                return withCreated.apply(asLongStream.apply(inputs));
                            }
                        })
        );
    }
}
