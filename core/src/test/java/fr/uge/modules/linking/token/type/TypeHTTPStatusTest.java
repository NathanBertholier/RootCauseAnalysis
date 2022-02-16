package fr.uge.modules.linking.token.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TypeHTTPStatusTest {
    @Test
    void regexMatchValues() {
        TypeHTTPStatus typeHTTPStatus = new TypeHTTPStatus();
        assertAll(
                () -> assertTrue("Hit".matches(typeHTTPStatus.getRegex())),
                () -> assertTrue("RefreshHit".matches(typeHTTPStatus.getRegex())),
                () -> assertTrue("Miss".matches(typeHTTPStatus.getRegex())),
                () -> assertTrue("LimitExceeded".matches(typeHTTPStatus.getRegex())),
                () -> assertTrue("CapacityExceeded".matches(typeHTTPStatus.getRegex())),
                () -> assertTrue("Error".matches(typeHTTPStatus.getRegex())),
                () -> assertTrue("Redirect".matches(typeHTTPStatus.getRegex())));
    }

    @Test
    void regexNotMatchValue() {
        TypeHTTPStatus typeHTTPStatus = new TypeHTTPStatus();
        assertAll(
                () -> assertFalse("HIt".matches(typeHTTPStatus.getRegex())),
                () -> assertFalse("RefreshHitt".matches(typeHTTPStatus.getRegex())),
                () -> assertFalse("Mess".matches(typeHTTPStatus.getRegex())),
                () -> assertFalse("Limit Exceeded".matches(typeHTTPStatus.getRegex())),
                () -> assertFalse("Capacity_Exceeded".matches(typeHTTPStatus.getRegex())));
    }
}
