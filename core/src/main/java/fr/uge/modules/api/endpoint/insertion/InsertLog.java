package fr.uge.modules.api.endpoint.insertion;

import fr.uge.modules.api.model.entities.RawLog;
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
import java.util.List;
import java.util.logging.Logger;

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
    public Uni<Response> insertLog(RawLog input) {
        return Panache.<RawLog>withTransaction(input::persist)
                .map(item -> {
                    emitter.send(item);
                    return Response
                                .created(URI.create("/insertlog/single/"))
                                .entity(item.id)
                                .build();
                        }
                );
    }


    @Path("/batch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> insertLog(List<RawLog> inputs) {
        inputs.forEach(input -> Panache.<RawLog>withTransaction(input::persist)
                .onFailure().invoke(() -> logger.severe("ERROR while inserting in database Rawlog"))
                .await().indefinitely());

        var response = Response
                .created(URI.create("/insertlog/batch"));
        inputs.forEach(input -> {
            response.entity(input);
            emitter.send(input);
        });
        return Uni.createFrom().item(response.build());
    }

}

