package fr.uge.modules.api.endpoint.token;

import fr.uge.modules.api.model.entities.TokenTypeEntity;
import fr.uge.modules.linking.token.type.TokenType;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/tokentypes")
public class TokenTypeEndpoint {
    @GET
    public Uni<List<String>> getTokenTypes() {
        return PanacheEntityBase
                .<TokenTypeEntity>listAll()
                .map(list -> list.stream().map(tokenTypeEntity -> tokenTypeEntity.name).toList());
    }
}
