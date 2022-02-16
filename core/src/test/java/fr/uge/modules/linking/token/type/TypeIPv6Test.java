package fr.uge.modules.linking.token.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TypeIPv6Test {
    @Test
    void regexMatchValues() {
        TypeIPv6 typeIPv6 = new TypeIPv6();
        assertAll(
                () -> assertTrue("2001:0620:0000:0000:0211:24FF:FE80:C12C".matches(typeIPv6.getRegex())),
                () -> assertTrue("2001:620:0:0:211:24FF:FE80:C12C".matches(typeIPv6.getRegex())),
                () -> assertTrue("2001:620::211:24FF:FE80:C12C".matches(typeIPv6.getRegex())));
    }

    @Test
    void regexNotMatchValue() {
        TypeIPv6 typeIPv6 = new TypeIPv6();
        assertAll(
                () -> assertFalse("2001:0620:0000:0000:0211:24FF:FE80".matches(typeIPv6.getRegex())),
                () -> assertFalse("2001:0620:0000:0000:".matches(typeIPv6.getRegex())),
                () -> assertFalse("2001:0620:0000:0000:0211:24FF:FE80:C12C:".matches(typeIPv6.getRegex())),
                () -> assertFalse("2001:0620:0000:0000:0211".matches(typeIPv6.getRegex())),
                () -> assertFalse("2001:41D0:1:2E4e::/64".matches(typeIPv6.getRegex())),
                () -> assertFalse("2001:0620:0000:0000:0211:24FF:FE80:C12C:CFGH".matches(typeIPv6.getRegex())));
    }
}
