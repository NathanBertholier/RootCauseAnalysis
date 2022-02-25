package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.Objects;
import java.util.List;

public interface TokenType {

    String getName();

    String getRegex();

    Integer getTokenTypeId();

    default int matcher(String word) {
        Objects.requireNonNull(word);
        if (word.matches(getRegex())) {
            return getTokenTypeId();
        }
        return -1;
    }

    double computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight);

    static TokenType fromId(int id){
        return switch (id){
            case 1 -> new TypeIPv4();
            case 2 -> new TypeIPv6();
            case 3 -> new TypeHTTPStatus();
            case 4 -> new TypeDatetime();
            case 5 -> new TypeEdgeResponse();
            case 6 -> new TypeURL();
            default -> {throw new IllegalStateException("TokenType not recognized");}
        };
    }

    enum TokenTypeId {
        ID_IPV4(1),
        ID_IPV6(2),
        ID_STATUS(3),
        ID_DATETIME(4),
        ID_EDGERESPONSE(5),
        ID_URL(6);

        private final int id;

        TokenTypeId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
