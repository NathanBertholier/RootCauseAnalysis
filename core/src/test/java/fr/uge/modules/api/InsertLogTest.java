package fr.uge.modules.api;

import fr.uge.modules.api.model.entities.Log;
import fr.uge.modules.api.model.entities.RawLogEntity;
import fr.uge.modules.api.model.entities.Token;
import io.quarkus.test.junit.QuarkusTest;
import netscape.javascript.JSObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.*;

@QuarkusTest
public class InsertLogTest {
    @BeforeEach
    public void sanitizeDatabases() {
        RawLogEntity.deleteAll();
        Log.deleteAll();
        Token.deleteAll();
    }

    @Test
    public void insertShouldReturnIdWithStatus200() {
        Map<String,String> map = new HashMap<>();
        map.put("log","2020-06-15\t12:47:50\tCDG50-C1\t6542\t82.255.169.186\t400\tHit");
        Map<String,String> map2 = new HashMap<>();
        map2.put("log","2020-06-15\t12:47:50\tCDG50-C1\t6542\t82.255.169.186\t200\tHit");
        IntStream.range(0, 3).forEach(index -> given()
                .contentType("application/json")
                .body(List.of(map,map2))
                .when().post("http://localhost:8083/insert/batch")
                .then()
                .statusCode(201));
    }
}
