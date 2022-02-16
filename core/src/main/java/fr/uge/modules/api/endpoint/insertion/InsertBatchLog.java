package fr.uge.modules.api.endpoint.insertion;

import fr.uge.modules.api.model.entities.RawLogEntity;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.common.annotation.Blocking;
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

@Path("/insert/batch")
public class InsertBatchLog {

    private static final Logger logger = Logger.getGlobal();
    @Channel("logs") Emitter<RawLogEntity> emitter;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public Uni<Response> insertLog(List<RawLogEntity> inputs) {
        inputs.forEach(input -> Panache.<RawLogEntity>withTransaction(input::persist)
                .onFailure().invoke(() -> logger.severe("ERROR while inserting in database Rawlog"))
                .await().indefinitely());

        inputs.forEach(System.out::println);
        var response = Response
                .created(URI.create("/insertlog/batch"));
        inputs.forEach(input -> {
            response.entity(input);
            emitter.send(input);
        });
        return Uni.createFrom().item(response.build());
    }

}
