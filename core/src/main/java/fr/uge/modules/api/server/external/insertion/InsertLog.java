package fr.uge.modules.api.server.external.insertion;

import fr.uge.modules.api.server.external.model.Log;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.IntStream;

@Path("/insertlog")
public class InsertLog {
    @Channel("logs-requests") Emitter<Log> emitter;
    private static int id = 0; // [AtomicInt, Uni] + déplacement dans un processor à prévoir

    @Path("/single")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Integer insertLog(Log input) {
        emitter.send(input);
        return id++;
    }

    @Path("/batch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertLog(List<Log> inputs) {
        int current = id;
        inputs.forEach(log -> {
            emitter.send(log);
            id++;
        });
        return Response.ok(IntStream.range(current, id).boxed().toList(), MediaType.APPLICATION_JSON)
                .build();
    }

}

