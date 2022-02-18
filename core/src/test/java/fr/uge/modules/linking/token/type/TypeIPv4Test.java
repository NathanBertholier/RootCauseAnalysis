package fr.uge.modules.linking.token.type;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TypeIPv4Test {

    @Test
    void nullValue() {
        TypeIPv4 typeIPv4 = new TypeIPv4();
        assertThrows(NullPointerException.class,() -> typeIPv4.matcher(null));
    }

    @Test
    void regexMatchValues() {
        TypeIPv4 typeIPv4 = new TypeIPv4();
        assertAll(
                () -> assertEquals(typeIPv4.getTokenTypeId(), typeIPv4.matcher("10.50.49.13")),
                () -> assertEquals(typeIPv4.getTokenTypeId(), typeIPv4.matcher("172.16.1.23")),
                () -> assertEquals(typeIPv4.getTokenTypeId(), typeIPv4.matcher("192.168.1.34")),
                () -> assertEquals(typeIPv4.getTokenTypeId(), typeIPv4.matcher("224.0.0.1")),
                () -> assertEquals(typeIPv4.getTokenTypeId(), typeIPv4.matcher("240.0.0.1")));

        Random r = new Random();
        for(int i=0;i<10;i++){
            assertEquals(typeIPv4.getTokenTypeId(), typeIPv4.matcher(r.nextInt( 256)+"."+r.nextInt( 256)+"."+r.nextInt( 256)+"."+r.nextInt( 256)));
        }
    }

    @Test
    void regexNotMatchValue() {
        TypeIPv4 typeIPv4 = new TypeIPv4();
        assertAll(
                () -> assertEquals(-1, typeIPv4.matcher("192.168.1")),
                () -> assertEquals(-1, typeIPv4.matcher("192.168")),
                () -> assertEquals(-1, typeIPv4.matcher("192")),
                () -> assertEquals(-1, typeIPv4.matcher("192.168.1.34.34")),
                () -> assertEquals(-1, typeIPv4.matcher("256.256.256.256")),
                () -> assertEquals(-1, typeIPv4.matcher("192.168.1.256")));
    }
}
