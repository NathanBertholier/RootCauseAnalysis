package fr.uge.modules.linking.token.type;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class TypeDateTimeTest {

    @Test
    void computeProximityTest() {
        Timestamp t0 = Timestamp.valueOf("2020-03-12 12:15:30");
        Timestamp t0_copy = Timestamp.valueOf("2020-03-12 12:15:30");
        Timestamp t1 = Timestamp.valueOf("2020-03-12 10:15:30");
        Timestamp t2 = Timestamp.valueOf("2020-03-12 11:15:30");
        Timestamp t3 = Timestamp.valueOf("2020-03-12 12:15:00");
        Timestamp t4 = Timestamp.valueOf("2020-03-12 10:16:00");
        Timestamp t5 = Timestamp.valueOf("2020-03-12 12:00:00");
        Timestamp out = Timestamp.valueOf("1997-03-12 12:15:30");
        int delta = 7200;

        assertAll(
                //Same time
                () -> assertEquals(100, TypeDatetime.computeDateTimeProximity(t0, t0_copy, delta).proximity()),

                //Delta limit
                () -> assertEquals(0, TypeDatetime.computeDateTimeProximity(t0, t1, delta).proximity()),
                () -> assertEquals(0, TypeDatetime.computeDateTimeProximity(t0, out, delta).proximity()),

                //Half delta in both ways
                () -> assertEquals(50, TypeDatetime.computeDateTimeProximity(t2, t0, delta).proximity()),
                () -> assertEquals(50, TypeDatetime.computeDateTimeProximity(t0, t2, delta).proximity()),

                //Inside Delta
                () -> assertEquals(99.58, TypeDatetime.computeDateTimeProximity(t0, t3, delta).proximity()),
                () -> assertEquals(0.41, TypeDatetime.computeDateTimeProximity(t0, t4, delta).proximity()),
                () -> assertEquals(87.08, TypeDatetime.computeDateTimeProximity(t0, t5, delta).proximity())
        );

    }

}