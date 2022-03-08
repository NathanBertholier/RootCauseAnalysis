package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TypeURLTest {

    @Test
    void nullValue() {
        TypeURL typeURL = new TypeURL();
        assertThrows(NullPointerException.class,() -> typeURL.matcher(null));
    }

    @Test
    void regexMatch() {
        TypeURL typeURL = new TypeURL();
        assertAll(
            () -> assertEquals(typeURL.getTokenTypeId(),typeURL.matcher("https://www.centreon.com/blog/tuto-deployer-sa-supervision-rapidement-avec-les-plugin-packs/")),
            () -> assertEquals(typeURL.getTokenTypeId(),typeURL.matcher("https://www.centreon.com/")),
            () -> assertEquals(typeURL.getTokenTypeId(),typeURL.matcher("https://www.centreon.com/en/blog/glossary/anomaly-detection/")),
            () -> assertEquals(typeURL.getTokenTypeId(),typeURL.matcher("https://www.centreon.com/blog/comment-utiliser-interface-centreon-api-web/")),
            () -> assertEquals(typeURL.getTokenTypeId(),typeURL.matcher("https://www.centreon.com/current/en/img/undraw_online.svg")),
            () -> assertEquals(typeURL.getTokenTypeId(),typeURL.matcher("https://www.centreon.com/en/?s=Anomaly+Detection")),
            () -> assertEquals(typeURL.getTokenTypeId(),typeURL.matcher("http://www.centreon.com/editions/")),
            () -> assertEquals(typeURL.getTokenTypeId(),typeURL.matcher("http://www.centreon.com/editions"))
        );

    }

    @Test
    void regexNotMatchValue() {
        TypeURL typeURL = new TypeURL();
        assertEquals(-1,typeURL.matcher("https://static.rootcause.com/wp-content/themes/rootcausev2/style.css?x97&ver=4.0.201210\tMozilla/5.0%20(20Win64;%20x64;%20rv:77.0)%20Firefox/77.0"));
        assertEquals(-1,typeURL.matcher("www.rootcause.com/editions/"));
        assertEquals(-1,typeURL.matcher("htttp://blog.example.com"));
        assertEquals(-1,typeURL.matcher("ftp://255.255.255.255"));
        assertEquals(-1,typeURL.matcher("255.255.255.255"));
        assertEquals(-1,typeURL.matcher("/toto/tata/titi"));
    }

    @Test
    void proximity() {
        TypeURL typeURL = new TypeURL();
        TokenEntity urlPizzaTomate = new TokenEntity();
        urlPizzaTomate.setValue("https://www.rootcause.com/pizza/ingredients/tomate");

        TokenEntity urlPizzaBoeuf = new TokenEntity();
        urlPizzaBoeuf.setValue("https://www.rootcause.com/pizza/ingredients/boeuf");

        TokenEntity urlEssaiGratuit = new TokenEntity();
        urlEssaiGratuit.setValue("https://www.rootcause.com/essai/?madein=china&email=monmail@test.fr");

        TokenEntity urlSVG = new TokenEntity();
        urlSVG.setValue("https://www.rootcause.com/test/al/img/drawing.svg");

        TokenEntity urlEvilCorp = new TokenEntity();
        urlEvilCorp.setValue("https://www.evilcorp.com/fsociety/jackpot/bank-error");

        assertAll(
                // empty array
                () -> assertEquals( 50, typeURL.computeProximity(new ArrayList<>(), new ArrayList<>(){ { add( urlPizzaTomate ); } } ).getProximity() ),
                () -> assertEquals( 50, typeURL.computeProximity(new ArrayList<>(){ { add( urlPizzaTomate ); } }, new ArrayList<>() ).getProximity() ),
                () -> assertEquals( 50, typeURL.computeProximity(new ArrayList<>(), new ArrayList<>() ).getProximity() ),

                // case 1 element in array
                () -> assertEquals( 0, typeURL.computeProximity(new ArrayList<>(){ { add( urlPizzaTomate ); } }, new ArrayList<>(){ { add( urlEvilCorp ); } } ).getProximity() ),
                () -> assertEquals( 100, typeURL.computeProximity(new ArrayList<>(){ { add( urlPizzaTomate ); } }, new ArrayList<>(){ { add( urlPizzaTomate ); } } ).getProximity() ),
                () -> assertEquals( 75, typeURL.computeProximity(new ArrayList<>(){ { add( urlPizzaTomate ); } }, new ArrayList<>(){ { add( urlPizzaBoeuf ); } } ).getProximity() ),

                () -> assertEquals( 33.33, typeURL.computeProximity(new ArrayList<>(){ { add( urlEssaiGratuit ); } }, new ArrayList<>(){ { add( urlPizzaBoeuf ); } } ).getProximity() ),

                () -> assertEquals( 25, typeURL.computeProximity(new ArrayList<>(){ { add( urlPizzaTomate );add( urlPizzaBoeuf ); } }, new ArrayList<>(){ { add( urlSVG ); } } ).getProximity() )


        );
    }
}
