package fr.uge.modules.linking.token.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeDateTest {
    @Test
    void nullValue() {
        TypeDate typeDate = new TypeDate();
        assertThrows(NullPointerException.class,() -> typeDate.matcher(null));
    }

    @Test
    void regexMatch() {
        TypeDate typeDate = new TypeDate();
        assertAll(
                () -> assertEquals(typeDate.getTokenTypeId(),typeDate.matcher("2020-10-10")),
                () -> assertEquals(typeDate.getTokenTypeId(),typeDate.matcher("3020-12-31")),
                () -> assertEquals(typeDate.getTokenTypeId(),typeDate.matcher("2020-01-01"))
                );

    }

    @Test
    void regexNotMatch() {
        TypeDate typeDate = new TypeDate();
        assertAll(
                () -> assertEquals(-1,typeDate.matcher("2020-13-01")),
                () -> assertEquals(-1,typeDate.matcher("3020-12-32")),
                () -> assertEquals(-1,typeDate.matcher("200-01-01")),
                () -> assertEquals(-1,typeDate.matcher("2020 01 01"))
        );
    }
}