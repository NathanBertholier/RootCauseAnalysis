package fr.uge.modules.linking.token.type;

import fr.uge.modules.linking.token.Token;

import java.util.Objects;
import java.util.Optional;

public interface TokenType {

    String getName();

    String getRegex();

    float computeProximity(Token t1, Token t2);

    static Optional<TokenType> fromTokenTypeId(int tokentypeId) {
        return Optional.ofNullable(switch (tokentypeId) {
            case 1 -> new TypeIPv4();
            case 2 -> new TypeIPv6();
            case 3 -> new TypeHTTPStatus();
            case 4 -> new TypeDatetime();
            case 5 -> new TypeEdgeResponse();
            default -> null;
        });
    }

}
