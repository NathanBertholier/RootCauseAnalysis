package fr.uge.modules.linking.token.type;

import org.junit.jupiter.api.Test;

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
}
