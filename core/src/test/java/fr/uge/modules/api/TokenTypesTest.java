package fr.uge.modules.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class TokenTypesTest {
    @Test
    public void tokenTypesShouldReturnCorrectList() {
        given()
                .when().get("http://localhost:8083/tokentypes")
                .then()
                .statusCode(200)
                .body("size()",is(5));
    }
}
