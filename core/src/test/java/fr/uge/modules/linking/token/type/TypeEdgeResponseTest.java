package fr.uge.modules.linking.token.type;

import fr.uge.modules.linking.token.Token;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeEdgeResponseTest {
    @Test
    void regexMatchValues() {
        TypeEdgeResponse typeEdgeResponse = new TypeEdgeResponse();
        assertAll(
                () -> assertTrue("Hit".matches(typeEdgeResponse.getRegex())),
                () -> assertTrue("RefreshHit".matches(typeEdgeResponse.getRegex())),
                () -> assertTrue("Miss".matches(typeEdgeResponse.getRegex())),
                () -> assertTrue("LimitExceeded".matches(typeEdgeResponse.getRegex())),
                () -> assertTrue("CapacityExceeded".matches(typeEdgeResponse.getRegex())),
                () -> assertTrue("Error".matches(typeEdgeResponse.getRegex())),
                () -> assertTrue("Redirect".matches(typeEdgeResponse.getRegex())));
    }

    @Test
    void regexNotMatchValue() {
        TypeEdgeResponse typeEdgeResponse = new TypeEdgeResponse();
        assertAll(
                () -> assertFalse("HIt".matches(typeEdgeResponse.getRegex())),
                () -> assertFalse("RefreshHitt".matches(typeEdgeResponse.getRegex())),
                () -> assertFalse("Mess".matches(typeEdgeResponse.getRegex())),
                () -> assertFalse("Limit Exceeded".matches(typeEdgeResponse.getRegex())),
                () -> assertFalse("Capacity_Exceeded".matches(typeEdgeResponse.getRegex())));
    }
}