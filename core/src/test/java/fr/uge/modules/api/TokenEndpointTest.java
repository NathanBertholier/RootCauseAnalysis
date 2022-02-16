package fr.uge.modules.api;

import fr.uge.modules.api.model.TokenRequest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
class TokenEndpointTest {
/*
    @Test
    void getTokens() {
        TokenRequest tokenRequest = new TokenRequest(null, null, -1, null, 10);
        given().body(tokenRequest)
                .when().post("http://localhost:8083/tokens")
                .then()
                .statusCode(200)
                .body(is("[\"IPV4\",\"IPV6\",\"Statut\",\"Datetime\",\"EdgeResponse\"]"));
    }

 */
}