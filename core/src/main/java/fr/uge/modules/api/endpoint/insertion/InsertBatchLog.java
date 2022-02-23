package fr.uge.modules.api.endpoint.insertion;

import fr.uge.modules.api.model.entities.RawLogEntity;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

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
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.LongStream;

@Path("/insertlog")
public class InsertBatchLog {
    private static final Function<Object, Response> withCreated = entity -> Response.created(URI.create("/insert/batch")).entity(entity).build();
    private static final Supplier<Response> withServerError = () -> Response.serverError().build();
    private static final Function<List<RawLogEntity>, LongStream> asLongStream = inputs -> LongStream.of(inputs.stream().mapToLong(r -> r.id).toArray());

    private static final Logger logger = Logger.getGlobal();

    @Channel("token-out") Emitter<RawLogEntity> emitter;

    @Inject Validator rawLogValidator;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> insertLog(List<RawLogEntity> inputs) {
        var violations = inputs.stream()
                .map(rawLogValidator::validate)
                .filter(Predicate.not(Set::isEmpty))
                .flatMap(s -> s.stream().distinct())
                .map(ConstraintViolation::getMessage)
                .toList();

        if(!violations.isEmpty()) {
            return Uni.createFrom().item(Response.status(400).entity(violations).build());
        }

        return Panache.withTransaction(
                () -> RawLogEntity.persist(inputs)
                        .onItemOrFailure().transform((success, error) -> {
                            if(error != null) {
                                logger.severe("Error while inserting: " + error);
                                return withServerError.get();
                            } else {
                                logger.info("Inserted: " + inputs);
                                inputs.forEach(emitter::send);
                                return withCreated.apply(asLongStream.apply(inputs));
                            }
                        })
        );
    }
}
