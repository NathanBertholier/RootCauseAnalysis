package fr.uge.modules.api.endpoint.insertion;

import fr.uge.modules.api.model.entities.RawLogEntity;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * TODO - Update channels, Unis & Multi returned by processors
 */
@Path("/insert/single")
public class InsertSingleLog {
    private static final Function<Object, Response> withCreated = entity -> Response.created(URI.create("/insert/batch")).entity(entity).build();
    private static final Supplier<Response> withServerError = () -> Response.serverError().build();

    private static final Logger logger = Logger.getGlobal();
    @Channel("logs") Emitter<RawLogEntity> emitter;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> insertLog(RawLogEntity input) {
        return Panache.<RawLogEntity>withTransaction(input::persist)
                        .onItemOrFailure().transform((item, error) -> {
                            if(error != null) {
                                logger.severe("Errror while inserting: " + error);
                                return withServerError.get();
                            } else {
                                logger.info("Inserted: " + item);
                                emitter.send(item);
                                return withCreated.apply(item.id);
                            }
                        });
    }

}