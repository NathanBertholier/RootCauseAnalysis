package fr.uge.modules.api.endpoint.token;

import fr.uge.modules.api.model.entities.TokenTypeEntity;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/tokentypes")
public class TokenTypeEndpoint {
    @GET
    public Uni<List<TokenTypeEntity>> getTokenTypes() {
        return TokenTypeEntity.listAll();
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
