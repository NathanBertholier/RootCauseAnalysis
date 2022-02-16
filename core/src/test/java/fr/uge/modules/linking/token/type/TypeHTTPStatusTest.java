package fr.uge.modules.linking.token.type;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TypeHTTPStatusTest {
    @Test
    void nullValue() {
        TypeHTTPStatus typeHTTPStatus = new TypeHTTPStatus();
        assertThrows(NullPointerException.class,() -> typeHTTPStatus.matcher(null));
    }
    @Test
    void regexMatchValues() {
        TypeHTTPStatus typeHTTPStatus = new TypeHTTPStatus();
        Random r = new Random();
        for(int i=0;i<10;i++){
            var status = ""+r.nextInt(1,6)+r.nextInt(0,6)+r.nextInt(0,10);
            assertEquals(typeHTTPStatus.getTokenTypeId(),typeHTTPStatus.matcher(status));
        }
    }

    @Test
    void regexNotMatchValue() {
        TypeHTTPStatus typeHTTPStatus = new TypeHTTPStatus();
        Random r = new Random();
        for(int i=0;i<10;i++){
            var status = ""+r.nextInt(0,10)+r.nextInt(6,10)+r.nextInt(0,10);
            assertEquals(-1,typeHTTPStatus.matcher(status));
        }
    }
}
