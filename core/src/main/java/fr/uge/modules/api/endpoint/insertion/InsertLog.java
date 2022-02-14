package fr.uge.modules.api.endpoint.insertion;

import fr.uge.modules.api.model.entities.RawLog;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
    public long insertLog(RawLog input) {
        System.out.println(input);
        System.out.println(input.getId());
        emitter.send(input);
        return input.getId();
    }

    @Path("/batch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Ne fonctionne pas pour le moment, simple test
    public long[] insertLog(List<RawLog> inputs) {
        return inputs.stream().mapToLong(this::insertLog).toArray();
    }
}

