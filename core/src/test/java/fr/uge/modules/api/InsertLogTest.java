package fr.uge.modules.api;

import fr.uge.modules.api.model.entities.Log;
import fr.uge.modules.api.model.entities.RawLog;
import fr.uge.modules.api.model.entities.Token;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class InsertLogTest {
    @BeforeEach
    public void sanitizeDatabases() {
        RawLog.deleteAll();
        Log.deleteAll();
        Token.deleteAll();
    }

    @Test
    public void insertShouldReturnIdWithStatus200() {
        IntStream.range(0, 10).forEach(index -> given()
                .contentType("application/json")
                .body("[{\"log\": \"2020-06-15 12:47:50 CDG50-C1 6542 82.255.169.186 200\"}]")
                .when().post("http://localhost:8083/insert/batch")
                .then()
                .statusCode(200)
                .body(is(index)));
    }
}
