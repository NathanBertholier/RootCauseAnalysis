package fr.uge.modules.linking.token.type;

import fr.uge.modules.linking.token.Token;

import java.util.List;
import java.util.Objects;

public class TypeEdgeResponse implements TokenType {
    private static final String NAME = "TokenType";
    private static final String REGEX = "^((Hit)|(RefreshHit)|(Miss)|(LimitExceeded)|(CapacityExceeded)|(Error)|(Redirect))$";
    private static final List<String> ERRORS = List.of("Miss", "Error", "Redirect", "LimitExceeded", "CapacityExceeded");


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getRegex() {
        return REGEX;
    }

    @Override
    public float computeProximity(Token t1, Token t2) {
        if (t1 == t2) {
            return 100;
        } else if (ERRORS.contains(t1.getValue()) && ERRORS.contains(t2.getValue())) {
            return 50;
        }
        return 0;
    }
}
