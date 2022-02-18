package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.TokenModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeEdgeResponseTest {
    @Test
    void nullValue() {
        TypeEdgeResponse typeEdgeResponse = new TypeEdgeResponse();
        assertThrows(NullPointerException.class,() -> typeEdgeResponse.matcher(null));
    }

    @Test
    void regexMatchValues() {
        TypeEdgeResponse typeEdgeResponse = new TypeEdgeResponse();
        assertAll(
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("Hit")),
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("RefreshHit")),
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("Miss")),
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("LimitExceeded")),
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("CapacityExceeded")),
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("Error")),
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("Redirect")));
    }

    @Test
    void regexNotMatchValue() {
        TypeEdgeResponse typeEdgeResponse = new TypeEdgeResponse();
        assertAll(
                () -> assertEquals(-1, typeEdgeResponse.matcher("HIt")),
                () -> assertEquals(-1, typeEdgeResponse.matcher("RefreshHitt")),
                () -> assertEquals(-1, typeEdgeResponse.matcher("Mess")),
                () -> assertEquals(-1, typeEdgeResponse.matcher("Limit Exceeded")),
                () -> assertEquals(-1, typeEdgeResponse.matcher("Capacity_Exceeded")));
    }

    @Test
    void proximity() {
        TypeEdgeResponse typeEdgeResponse = new TypeEdgeResponse();/*
        assertAll(
                () -> assertEquals(100, typeEdgeResponse.computeProximity(new TokenModel(typeEdgeResponse.getTokenTypeId(), "Hit"), new TokenModel(typeEdgeResponse.getTokenTypeId(), "Hit"))),
                () -> assertEquals(50, typeEdgeResponse.computeProximity(new TokenModel(typeEdgeResponse.getTokenTypeId(), "Miss"), new TokenModel(typeEdgeResponse.getTokenTypeId(), "Error"))),
                () -> assertEquals(0, typeEdgeResponse.computeProximity(new TokenModel(typeEdgeResponse.getTokenTypeId(), "Hit"), new TokenModel(typeEdgeResponse.getTokenTypeId(), "RefreshHit")))
        );*/

    }
}