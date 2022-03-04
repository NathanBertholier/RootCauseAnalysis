package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TypeResourceTest {

    @Test
    void nullValue() {
        TypeResource typeResource = new TypeResource();
        assertThrows(NullPointerException.class,() -> typeResource.matcher(null));
    }

    @Test
    void regexMatch() {
        TypeResource typeResource = new TypeResource();
        assertAll(
            () -> assertEquals(typeResource.getTokenTypeId(), typeResource.matcher("/wp-content/themes/Centreonv2/assets/fonts/radikal/radikal-webfont.ttf")),
            () -> assertEquals(typeResource.getTokenTypeId(),typeResource.matcher("/wp-includes/css/dist/block-library/style.min.css.gzip")),
            () -> assertEquals(typeResource.getTokenTypeId(),typeResource.matcher("/robots.txt")),
            () -> assertEquals(typeResource.getTokenTypeId(),typeResource.matcher("/favicon.ico")),
            () -> assertEquals(typeResource.getTokenTypeId(),typeResource.matcher("/wp-content/uploads/2019/03/aws-refarch-drupal-v20170713-1024x680.png")),
            () -> assertEquals(typeResource.getTokenTypeId(),typeResource.matcher("/wp-content/themes/Divi/style.css.gzip")),
            () -> assertEquals(typeResource.getTokenTypeId(),typeResource.matcher("/wp-content/plugins/sitepress-multilingual-cms/templates/language-switchers/legacy-dropdown/style.css.gzip"))
        );

    }

    @Test
    void regexNotMatchValue() {
        TypeURL typeResource = new TypeURL();
        assertEquals(-1,typeResource.matcher("/wp-object\tMozilla/20100101%20Firefox/77.0"));
        assertEquals(-1,typeResource.matcher("www.centreon.com/editions/"));
        /*assertEquals(-1,typeResource.matcher("http://centreon.com/"));*/
        assertEquals(-1,typeResource.matcher("255.255.255.255"));
    }

}
