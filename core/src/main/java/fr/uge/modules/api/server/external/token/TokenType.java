package fr.uge.modules.api.server.external.token;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Collections;
import java.util.List;

@Path("/tokentypes")
public class TokenType {
    @GET
    public Uni<List<String>> getTokenTypes(){
        return Uni.createFrom().item(Collections.emptyList()); // TODO - query db
    }
}
