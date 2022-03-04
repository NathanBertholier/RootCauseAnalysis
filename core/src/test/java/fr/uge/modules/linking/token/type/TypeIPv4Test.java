package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
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

    @Test
    void computeProximity() {
        TypeIPv4 typeIPv4 = new TypeIPv4();
        TokenEntity t0 = new TokenEntity();
        TokenEntity t1 = new TokenEntity();
        TokenEntity t2 = new TokenEntity();
        TokenEntity t3 = new TokenEntity();
        TokenEntity t4 = new TokenEntity();
        TokenEntity t5 = new TokenEntity();
        t0.value = "10.11.12.13";
        t1.value = "100.150.150.150";
        t2.value = "10.150.150.150";
        t3.value = "10.11.150.150";
        t4.value = "10.11.12.150";
        t5.value = "10.11.12.13";

        /*
        assertAll(
                () -> assertEquals(100, typeIPv4.computeProximity(new ArrayList<>(List.of(t0)), new ArrayList<>(List.of(t0))).proximity()),
                () -> assertEquals(100, typeIPv4.computeProximity(new ArrayList<>(List.of(t1)), new ArrayList<>(List.of(t1))).proximity()),
                () -> assertEquals(0, typeIPv4.computeProximity(new ArrayList<>(List.of(t0)), new ArrayList<>(List.of(t1))).proximity()),
                () -> assertEquals(20, typeIPv4.computeProximity(new ArrayList<>(List.of(t0)), new ArrayList<>(List.of(t2))).proximity()),
                () -> assertEquals(85, typeIPv4.computeProximity(new ArrayList<>(List.of(t0)), new ArrayList<>(List.of(t3))).proximity()),
                () -> assertEquals(95, typeIPv4.computeProximity(new ArrayList<>(List.of(t0)), new ArrayList<>(List.of(t4))).proximity()),
                () -> assertEquals(50, typeIPv4.computeProximity(new ArrayList<>(List.of(t0)), new ArrayList<>(List.of(t0, t1))).proximity()),
                () -> assertEquals(50, typeIPv4.computeProximity(new ArrayList<>(List.of(t0, t1)), new ArrayList<>(List.of(t1))).proximity()),
                () -> assertEquals(50, typeIPv4.computeProximity(new ArrayList<>(List.of(t0, t1)), new ArrayList<>(List.of(t0, t1))).proximity()),
                () -> assertEquals(50, typeIPv4.computeProximity(new ArrayList<>(List.of(t1 ,t0)), new ArrayList<>(List.of(t1, t0))).proximity()),
                () -> assertEquals(50, typeIPv4.computeProximity(new ArrayList<>(List.of(t1 ,t1)), new ArrayList<>(List.of(t0, t1))).proximity()),
                () -> assertEquals(91.25, typeIPv4.computeProximity(new ArrayList<>(List.of(t3 ,t4)), new ArrayList<>(List.of(t5, t3))).proximity())
        );

         */
    }
}
