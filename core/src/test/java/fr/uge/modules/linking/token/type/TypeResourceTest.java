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
            () -> assertEquals(typeResource.getTokenTypeId(), typeResource.matcher("/wp-content/themes/Rootcausev2/assets/fonts/radikal/radikal-webfont.ttf")),
            () -> assertEquals(typeResource.getTokenTypeId(),typeResource.matcher("/wp-includes/css/dist/block-library/style.min.css.gzip")),
            () -> assertEquals(typeResource.getTokenTypeId(),typeResource.matcher("/mecha.txt")),
            () -> assertEquals(typeResource.getTokenTypeId(),typeResource.matcher("/favicon.ico")),
            () -> assertEquals(typeResource.getTokenTypeId(),typeResource.matcher("/wp-content/uploads/2016/03/architecture-france-1024x680.png")),
            () -> assertEquals(typeResource.getTokenTypeId(),typeResource.matcher("/wp-content/themes/Divi/style.css.gzip")),
            () -> assertEquals(typeResource.getTokenTypeId(),typeResource.matcher("/wp-content/plugins/sitepress-multilingual-cms/templates/language-switchers/legacy/style.css.gzip"))
        );

    }

    @Test
    void regexNotMatchValue() {
        TypeResource typeResource = new TypeResource();
        assertEquals(-1,typeResource.matcher("/wp-object\tMozilla/201004564654%20Firefox/77.0"));
        assertEquals(-1,typeResource.matcher("www.rootcause.com/editions/"));
        assertEquals(-1,typeResource.matcher("255.255.255.255"));
    }

    @Test
    void proximity() {
        TypeResource typeResource = new TypeResource();
        TokenEntity resRadikal = new TokenEntity();
        resRadikal.setValue("/wp-content/themes/Rootcause/assets/fonts/kebab-webfont.ttf");

        TokenEntity resLibrary = new TokenEntity();
        resLibrary.setValue("/wp-includes/css/dist/block-library/style.min.css.gzip");

        TokenEntity resCaption = new TokenEntity();
        resCaption.setValue("/wp-content/plugins/amazing-hover-effects-pro/css/caption.css.gzip");

        TokenEntity resLegacy = new TokenEntity();
        resLegacy.setValue("/wp-content/plugins/sitepress-multilingual-cms/templates/language-routers/legacy/style.css.gzip");


        assertAll(
                // empty array
                () -> assertEquals( 50, typeResource.computeProximity(new ArrayList<>(), new ArrayList<>(){ { add( resRadikal ); } } ).getProximity() ),
                () -> assertEquals( 50, typeResource.computeProximity(new ArrayList<>(){ { add( resRadikal ); } }, new ArrayList<>() ).getProximity() ),
                () -> assertEquals( 50, typeResource.computeProximity(new ArrayList<>(), new ArrayList<>() ).getProximity() ),

                // case 1 element in array
                () -> assertEquals( 100, typeResource.computeProximity(new ArrayList<>(){ { add( resRadikal ); } }, new ArrayList<>(){ { add( resRadikal ); } } ).getProximity() ),
                () -> assertEquals( 0, typeResource.computeProximity(new ArrayList<>(){ { add( resRadikal ); } }, new ArrayList<>(){ { add( resLibrary ); } } ).getProximity() ),
                () -> assertEquals( 16.66, typeResource.computeProximity(new ArrayList<>(){ { add( resRadikal ); } }, new ArrayList<>(){ { add( resCaption ); } } ).getProximity() ),
                () -> assertEquals( 33.33, typeResource.computeProximity(new ArrayList<>(){ { add( resLegacy ); } }, new ArrayList<>(){ { add( resCaption ); } } ).getProximity() ),

                //  case 2 element in array
                () -> assertEquals( 8.33, typeResource.computeProximity(new ArrayList<>(){ { add( resCaption );add( resLibrary ); } }, new ArrayList<>(){ { add( resRadikal ); } } ).getProximity() )


        );



    }

}
