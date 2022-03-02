package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TypeURLTest {

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
        assertEquals(-1,typeURL.matcher("https://static.centreon.com/wp-content/themes/Centreonv2/style.css.gzip?x97250&ver=4.0.20191210\tMozilla/5.0%20(Windows%20NT%2010.0;%20Win64;%20x64;%20rv:77.0)%20Gecko/20100101%20Firefox/77.0"));
        assertEquals(-1,typeURL.matcher("www.centreon.com/editions/"));
        assertEquals(-1,typeURL.matcher("http://centreon.com/"));
        assertEquals(-1,typeURL.matcher("http://blog.example.com"));
        assertEquals(-1,typeURL.matcher("http://255.255.255.255"));
        assertEquals(-1,typeURL.matcher("255.255.255.255"));
    }

    @Test
    void proximity() {
        TypeURL typeURL = new TypeURL();
        // TODO : here some test case maybe add more
        TokenEntity urlPizzaTomate = new TokenEntity();
        urlPizzaTomate.setValue("https://www.centreon.com/pizza/ingredients/tomate");

        TokenEntity urlPizzaBoeuf = new TokenEntity();
        urlPizzaBoeuf.setValue("https://www.centreon.com/pizza/ingredients/boeuf");

        TokenEntity urlEssaiGratuit = new TokenEntity();
        urlEssaiGratuit.setValue("https://www.centreon.com/essai-gratuit/?origine=community&email=marcos.rodrigues@terceiro.rnp.br");

        TokenEntity urlSVG = new TokenEntity();
        urlSVG.setValue("https://www.centreon.com/current/en/img/undraw_online.svg");

        /*
        assertAll(
                // empty array
                () -> assertEquals( 50, typeURL.computeProximity(new ArrayList<>(), new ArrayList<>(){ { add( urlPizzaTomate ); } } ).proximity() ),
                () -> assertEquals( 50, typeURL.computeProximity(new ArrayList<>(){ { add( urlPizzaTomate ); } }, new ArrayList<>() ).proximity() ),
                () -> assertEquals( 50, typeURL.computeProximity(new ArrayList<>(), new ArrayList<>() ).proximity() ),

                // case 1 element in array
                () -> assertEquals( 100, typeURL.computeProximity(new ArrayList<>(){ { add( urlPizzaTomate ); } }, new ArrayList<>(){ { add( urlPizzaTomate ); } } ).proximity() ),
                () -> assertEquals( 75, typeURL.computeProximity(new ArrayList<>(){ { add( urlPizzaTomate ); } }, new ArrayList<>(){ { add( urlPizzaBoeuf ); } } ).proximity() ),

                () -> assertEquals( 33.33333333333333, typeURL.computeProximity(new ArrayList<>(){ { add( urlEssaiGratuit ); } }, new ArrayList<>(){ { add( urlPizzaBoeuf ); } } ).proximity() ),


                () -> assertEquals( 25, typeURL.computeProximity(new ArrayList<>(){ { add( urlPizzaTomate );add( urlPizzaBoeuf ); } }, new ArrayList<>(){ { add( urlSVG ); } } ).proximity() )


        );

         */

    }
}
