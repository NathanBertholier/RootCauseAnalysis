package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TypeIPv6Test {

    @Test
    void nullValue() {
        TypeIPv6 typeIPv6 = new TypeIPv6();
        assertThrows(NullPointerException.class,() -> typeIPv6.matcher(null));
    }

    private String generatorIPv6(){
        Random r = new Random();
        StringJoiner s = new StringJoiner(":");
        for(int i = 0;i<8;i++){
            s.add(generatorHex(r.nextInt(4)+1));
        }
        return s.toString();
    }
    private String generatorHex(int nb){
        StringBuilder s = new StringBuilder();
        Random r = new Random();
        for(int i=0;i<nb;i++ ){
            s.append(Integer.toHexString(r.nextInt(16)));
        }
        if(s.equals("OOOO")){
            return "";
        }
        return s.toString();
    }
    @Test
    void regexMatchValues() {
        TypeIPv6 typeIPv6 = new TypeIPv6();
        assertAll(
                () -> assertEquals(typeIPv6.getTokenTypeId(), typeIPv6.matcher("2001:0620:0000:0000:0211:24FF:FE80:C12C")),
                () -> assertEquals(typeIPv6.getTokenTypeId(), typeIPv6.matcher("2001:620:0:0:211:24FF:FE80:C12C")),
                () -> assertEquals(typeIPv6.getTokenTypeId(), typeIPv6.matcher("2001:620::211:24FF:FE80:C12C")));
        for(int i=0;i<10;i++){
            assertTrue(
                    generatorIPv6().matches(typeIPv6.getRegex()));
        }
    }

    @Test
    void regexNotMatchValue() {
        TypeIPv6 typeIPv6 = new TypeIPv6();
        assertAll(
                () -> assertEquals(-1, typeIPv6.matcher("2001:0620:0000:0000:0211:24FF:FE80")),
                () -> assertEquals(-1, typeIPv6.matcher("2001:0620:0000:0000:")),
                () -> assertEquals(-1, typeIPv6.matcher("2001:0620:0000:0000:0211")),
                () -> assertEquals(-1, typeIPv6.matcher("2001:41D0:1:2E4e::/64")),
                () -> assertEquals(-1, typeIPv6.matcher("2001:0620:0000:0000:0211:24FF:FE80:C12C:CFGH")),
                () -> assertEquals(-1, typeIPv6.matcher("2001:0620:0000:0000:0211:24FF:FE80:C12C:")));
    }

    @Test
    void proximity() {
        TypeIPv6 typeIPv6 = new TypeIPv6();
        TokenEntity firstIPv6 = new TokenEntity();
        firstIPv6.setValue( generatorIPv6() );
        TokenEntity secondtIPv6 = new TokenEntity();
        secondtIPv6.setValue( generatorIPv6() );
        TokenEntity thirdIPv6 = new TokenEntity();
        thirdIPv6.setValue( generatorIPv6() );

        /*
        assertAll(
            // empty array
            () -> assertEquals(0,   typeIPv6.computeProximity( new ArrayList<>(){ { add( firstIPv6 ); } },
                    new ArrayList<>() ).getProximity() ),
            () -> assertEquals(0,   typeIPv6.computeProximity( new ArrayList<>(),
                    new ArrayList<>(){ { add(firstIPv6); } } ).getProximity() ),
            () -> assertEquals(0,   typeIPv6.computeProximity( new ArrayList<>(),
                    new ArrayList<>() ).proximity() ),

            // Case 1 element
            () -> assertEquals(100, typeIPv6.computeProximity( new ArrayList<>(){ { add( firstIPv6 ); } },
                    new ArrayList<>(){ { add( firstIPv6 ); } } ).proximity() ),
            () -> assertEquals(100, typeIPv6.computeProximity( new ArrayList<>(){ { add( secondtIPv6 ); } },
                    new ArrayList<>(){ { add(secondtIPv6); } } ).proximity() ),
            () -> assertEquals(0,   typeIPv6.computeProximity( new ArrayList<>(){ { add( firstIPv6 ); } },
                    new ArrayList<>(){ { add(secondtIPv6); } } ).proximity() ),

            // Case 2 element
            () -> assertEquals(50, typeIPv6.computeProximity( new ArrayList<>(){ { add( firstIPv6 ); } },
                    new ArrayList<>(){ { add( firstIPv6 );add(secondtIPv6); } } ).proximity() ),
            () -> assertEquals(50, typeIPv6.computeProximity( new ArrayList<>(){ { add( firstIPv6 ); add( secondtIPv6 ); } },
                    new ArrayList<>(){ { add(firstIPv6); } } ).proximity() ),
            () -> assertEquals(100,   typeIPv6.computeProximity( new ArrayList<>(){ { add( firstIPv6 ); add( secondtIPv6 ); } },
                    new ArrayList<>(){ { add(secondtIPv6);add(firstIPv6); } } ).proximity() ),

            // Case 3 element
            () -> assertEquals(100d/3, typeIPv6.computeProximity( new ArrayList<>(){ { add( firstIPv6 );add( secondtIPv6 );add( thirdIPv6 ); } },
                    new ArrayList<>(){ { add( firstIPv6 ); } } ).proximity() ),
            () -> assertEquals(200d/3, typeIPv6.computeProximity( new ArrayList<>(){ { add( firstIPv6 );add( secondtIPv6 );add( thirdIPv6 ); } },
                    new ArrayList<>(){ { add( firstIPv6 );add(thirdIPv6); } } ).proximity() ),
            () -> assertEquals(100, typeIPv6.computeProximity( new ArrayList<>(){ { add( firstIPv6 );add( secondtIPv6 );add( thirdIPv6 ); } },
                    new ArrayList<>(){ { add( thirdIPv6 );add(secondtIPv6);add( firstIPv6 ); } } ).proximity() )

        );

         */
    }
}
