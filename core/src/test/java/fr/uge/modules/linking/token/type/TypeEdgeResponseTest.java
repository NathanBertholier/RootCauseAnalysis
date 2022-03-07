package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TypeEdgeResponseTest {
    @Test
    void nullValue() {
        TypeEdgeResponse typeEdgeResponse = new TypeEdgeResponse();
        assertThrows(NullPointerException.class,() -> typeEdgeResponse.matcher(null));
    }

    @Test
    void regexMatchValues() {
        TypeEdgeResponse typeEdgeResponse = new TypeEdgeResponse();
        assertAll(
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("Hit")),
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("RefreshHit")),
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("Miss")),
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("LimitExceeded")),
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("CapacityExceeded")),
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("Error")),
                () -> assertEquals(typeEdgeResponse.getTokenTypeId(), typeEdgeResponse.matcher("Redirect")));
    }

    @Test
    void regexNotMatchValue() {
        TypeEdgeResponse typeEdgeResponse = new TypeEdgeResponse();
        assertAll(
                () -> assertEquals(-1, typeEdgeResponse.matcher("HIt")),
                () -> assertEquals(-1, typeEdgeResponse.matcher("RefreshHitt")),
                () -> assertEquals(-1, typeEdgeResponse.matcher("Mess")),
                () -> assertEquals(-1, typeEdgeResponse.matcher("Limit Exceeded")),
                () -> assertEquals(-1, typeEdgeResponse.matcher("Capacity_Exceeded")));
    }

    @Test
    void proximity() {
        TypeEdgeResponse typeEdgeResponse = new TypeEdgeResponse();
        TokenEntity hit = new TokenEntity();
        hit.setValue("Hit");
        TokenEntity refreshHit = new TokenEntity();
        refreshHit.setValue("RefreshHit");

        TokenEntity miss = new TokenEntity();
        miss.setValue("Miss");
        TokenEntity error = new TokenEntity();
        error.setValue("Error");


        assertAll(

                // empty array
                () -> assertEquals( 50, typeEdgeResponse.computeProximity(new ArrayList<>(),                        new ArrayList<>(){{ add( hit ); }} ).getProximity() ),
                () -> assertEquals( 50, typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( miss ); }},      new ArrayList<>() ).getProximity() ),
                () -> assertEquals( 50, typeEdgeResponse.computeProximity(new ArrayList<>(),                        new ArrayList<>() ).getProximity() ),

                // 100 % identical
                () -> assertEquals( 100, typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( hit ); }},      new ArrayList<>(){{ add( hit ); }} ).getProximity() ),
                () -> assertEquals( 100, typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( error ); }},    new ArrayList<>(){{ add( error ); }} ).getProximity() ),

                // error case
                () -> assertEquals( 0,  typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( error ); }},     new ArrayList<>(){{ add( hit ); }} ).getProximity() ),
                () -> assertEquals( 95, typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( error ); }},     new ArrayList<>(){{ add( miss ); }} ).getProximity() ),

                // no error case
                () -> assertEquals( 95, typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( hit ); }},       new ArrayList<>(){{ add( refreshHit ); }} ).getProximity() ),
                () -> assertEquals( 25, typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( refreshHit ); }},new ArrayList<>(){{ add( error ); }} ).getProximity() ),

                // case 2 elements in target array
                () -> assertEquals( 50,         typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( hit );add( error ); }},  new ArrayList<>(){{ add( hit ); }} ).getProximity() ),
                () -> assertEquals( 125.f/2,    typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( hit );add( error ); }},  new ArrayList<>(){{ add( error ); }} ).getProximity() ),
                () -> assertEquals( 95.f/2,     typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( hit );add( error ); }},  new ArrayList<>(){{ add( refreshHit ); }} ).getProximity() ),
                () -> assertEquals( 120.f/2,    typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( hit );add( error ); }},  new ArrayList<>(){{ add( miss ); }} ).getProximity() ),

                // case 2 element in linking
                () -> assertEquals( 62.5,        typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( hit ); }},       new ArrayList<>(){{ add( hit );add( error ); }} ).getProximity() ),
                () -> assertEquals( 50,        typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( error ); }},     new ArrayList<>(){{ add( hit );add( error ); }} ).getProximity() ),
                () -> assertEquals( 60,         typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( refreshHit ); }},new ArrayList<>(){{ add( hit );add( error ); }} ).getProximity() ),
                () -> assertEquals( 47.5,         typeEdgeResponse.computeProximity(new ArrayList<>(){{ add( miss ); }},      new ArrayList<>(){{ add( hit );add( error ); }} ).getProximity() )
        );


    }
}