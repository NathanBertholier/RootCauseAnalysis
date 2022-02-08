package fr.uge.modules.api.server.external.token;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/tokentypes")
public class TokenType {
    @GET
    public Uni<List<String>> getTokenTypes(){
        return Uni.createFrom().item(getAllTokenTypes());
    }

    private static List<String> getAllTokenTypes(){
        return List.of(
                "TypeDate",
                "TypeDatetime",
                "TypeIPv4",
                "TypeIPv6",
                "TypeTime"
        );
    }
}
