package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(-1,typeHTTPStatus.matcher("000"));
        assertEquals(-1,typeHTTPStatus.matcher("5"));
    }
    @Test
    void proximity() {

        // TODO : here some test case maybe add more
        TypeHTTPStatus typeHTTPStatus = new TypeHTTPStatus();
        TokenEntity tokenStatus200 = new TokenEntity();
        tokenStatus200.setValue("200");
        TokenEntity tokenStatus204 = new TokenEntity();
        tokenStatus204.setValue("204");
        TokenEntity tokenStatus300 = new TokenEntity();
        tokenStatus300.setValue("300");
        TokenEntity tokenStatus400 = new TokenEntity();
        tokenStatus400.setValue("400");
        TokenEntity tokenStatus402 = new TokenEntity();
        tokenStatus402.setValue("402");
        TokenEntity tokenStatus500 = new TokenEntity();
        tokenStatus500.setValue("500");

        /*
        assertAll(
                // empty array
                () -> assertEquals( 0, typeHTTPStatus.computeProximity(new ArrayList<>(), new ArrayList<>(){ { add( tokenStatus200 ); } } ).proximity() ),
                () -> assertEquals( 0, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus200 ); }}, new ArrayList<>() ).proximity() ),
                () -> assertEquals( 0, typeHTTPStatus.computeProximity(new ArrayList<>(), new ArrayList<>() ).proximity() ),
                // 100% identical
                () -> assertEquals( 100, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus200 ); }}, new ArrayList<>(){ { add( tokenStatus200 ); } } ).proximity() ),
                () -> assertEquals( 100, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus300 ); }}, new ArrayList<>(){ { add( tokenStatus300 ); } } ).proximity() ),
                () -> assertEquals( 100, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus400 ); }}, new ArrayList<>(){ { add( tokenStatus400 ); } } ).proximity() ),
                () -> assertEquals( 100, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus500 ); }}, new ArrayList<>(){ { add( tokenStatus500 ); } } ).proximity() ),
                () -> assertEquals( 100, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus402 ); }}, new ArrayList<>(){ { add( tokenStatus402 ); } } ).proximity() ),

                //4xx ou 5xx
                () -> assertEquals( 95, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus400 ); }}, new ArrayList<>(){ { add( tokenStatus402 ); } } ).proximity() ),
                () -> assertEquals( 90, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus400 ); }}, new ArrayList<>(){ { add( tokenStatus500 ); } } ).proximity() ),
                () -> assertEquals( 90, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus500 ); }}, new ArrayList<>(){ { add( tokenStatus402 ); } } ).proximity() ),
                () -> assertEquals( 0, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus400 ); }}, new ArrayList<>(){ { add( tokenStatus200 ); } } ).proximity() ),
                () -> assertEquals( 0, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus400 ); }}, new ArrayList<>(){ { add( tokenStatus300 ); } } ).proximity() ),

                //2xx ou 3xx
                () -> assertEquals( 95, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus204 ); }}, new ArrayList<>(){ { add( tokenStatus200 ); } } ).proximity() ),
                () -> assertEquals( 25, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus204 ); }}, new ArrayList<>(){ { add( tokenStatus402 ); } } ).proximity() ),
                () -> assertEquals( 25, typeHTTPStatus.computeProximity(new ArrayList<>(){ { add( tokenStatus204 ); }}, new ArrayList<>(){ { add( tokenStatus500 ); } } ).proximity() )
        );

         */

    }
}
