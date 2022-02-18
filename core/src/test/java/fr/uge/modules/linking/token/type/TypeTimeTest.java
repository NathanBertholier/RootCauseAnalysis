package fr.uge.modules.linking.token.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeTimeTest {

    @Test
    void regexMatchValues() {
        TypeTime typeTime = new TypeTime();
        assertAll(
                () -> assertEquals(typeTime.getTokenTypeId(),typeTime.matcher("10:00:00")),
                () -> assertEquals(typeTime.getTokenTypeId(),typeTime.matcher("23:59:59")),
                () -> assertEquals(typeTime.getTokenTypeId(),typeTime.matcher("00:00:00"))
        );
    }

    @Test
    void regexNotMatchValues() {
        TypeTime typeTime = new TypeTime();
        assertAll(
                () -> assertEquals(-1,typeTime.matcher("10-00-00")),
                () -> assertEquals(-1,typeTime.matcher("24:00:00")),
                () -> assertEquals(-1,typeTime.matcher("-1:-1:-1"))
        );
    }
}