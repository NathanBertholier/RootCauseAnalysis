package fr.uge.modules.api.endpoint.token;

import fr.uge.modules.api.model.TokenType;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Path("/tokentypes")
public class TokenTypeEndpoint {
    @GET
    public Uni<List<TokenType>> getTokenTypes() {
        return TokenType.listAll();
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
