package fr.uge.modules.api;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class LinkRTest {
    @Test
    public void linkShouldReturnCorrectJSON() {
        given()
                .when().get("http://localhost:8083/link?delta=7200&id1=2&id2=3")
                .then()
                .statusCode(200)
                .body("computations[0].token_type",is("type"))
                .body("computations[0].value_log_first",is("value1"))
                .body("computations[0].value_log_second",is("value2"))
                .body("computations[0].proximity",is(1.0F))
                .body("proximity",is(1.05F));
    }
}
