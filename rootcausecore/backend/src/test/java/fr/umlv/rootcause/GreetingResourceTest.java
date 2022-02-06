package fr.umlv.rootcause;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class GreetingResourceTest {

    @org.junit.Test
    public void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("Hello RESTEasy"));
    }

    @Test
    void hello() {
        GreetingResource greetingResource = new GreetingResource();
        assertEquals("Hello RESTEasy",greetingResource.hello());
    }
}