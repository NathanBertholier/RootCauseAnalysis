package fr.uge.modules.linking.token.type;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TypeIPv4Test {
    @Test
    void regexMatchValues() {
        TypeIPv4 typeIPv4 = new TypeIPv4();
        assertAll(
                () -> assertTrue("10.50.49.13".matches(typeIPv4.getRegex())),
                () -> assertTrue("172.16.1.23".matches(typeIPv4.getRegex())),
                () -> assertTrue("192.168.1.34".matches(typeIPv4.getRegex())),
                () -> assertTrue("224.0.0.1".matches(typeIPv4.getRegex())),
                () -> assertTrue("240.0.0.1".matches(typeIPv4.getRegex())));
        Random r = new Random();
        for(int i=0;i<10;i++){
            assertTrue(
                    (r.nextInt( 256)+"."+r.nextInt( 256)+"."+r.nextInt( 256)+"."+r.nextInt( 256))
                            .matches(typeIPv4.getRegex()));
        }
    }

    @Test
    void regexNotMatchValue() {
        TypeIPv4 typeIPv4 = new TypeIPv4();
        assertAll(
                () -> assertFalse("192.168.1".matches(typeIPv4.getRegex())),
                () -> assertFalse("192.168".matches(typeIPv4.getRegex())),
                () -> assertFalse("192".matches(typeIPv4.getRegex())),
                () -> assertFalse("192.168.1.34.34".matches(typeIPv4.getRegex())),
                () -> assertFalse("192.168.1.256".matches(typeIPv4.getRegex())),
                () -> assertFalse("256.256.256.256".matches(typeIPv4.getRegex())));
    }
}
