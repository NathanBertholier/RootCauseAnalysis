package fr.uge.modules.api.endpoint;

import fr.uge.modules.api.model.TokenModel;
import fr.uge.modules.linking.token.type.TokenType;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/test/{id}")
public class Test {
    @Inject
    PgPool client;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<TokenModel>> getTokens(@PathParam("id") long id){
            var results = fetchTokens(id);
            System.out.println("Results linking: " + results);
            return results;
    }

    private Uni<List<TokenModel>> fetchTokens(long id) {
        var tokens = client
                .preparedQuery("SELECT * FROM token LEFT JOIN tokenType ON token.idtokentype = tokentype.id WHERE token.id = '" + id + "'")
                .mapping(row -> new TokenModel(
                        TokenType.fromTokenTypeId(row.getInteger("tokentypeid")).orElseThrow().getName(),
                        row.getString("value"))
                )
                .execute()
                .onItem().transformToMulti(Multi.createFrom()::iterable)
                .collect().asList();
        System.out.println("Tokens: " + tokens);
        return tokens;
    }
}
