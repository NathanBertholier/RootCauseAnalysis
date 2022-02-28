package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.Link;

import java.util.Collection;
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

    default Link computeProximity(List<TokenEntity> listTokensLeft, List<TokenEntity> listTokensRight){
        var computations = listTokensLeft.stream()
                .map(TokenEntity::getValue)
                .map(tokenLeftValue -> listTokensRight.stream()
                        .map(tokenRight -> {
                            var tokenRightValue = tokenRight.value;
                            return new Computation(this, tokenLeftValue, tokenRightValue, 0d);
                        }).toList()
                ).flatMap(Collection::stream)
                .toList();
        return new Link(computations, computations.stream().mapToDouble(Computation::proximity).sum() / computations.size());
    }

    static TokenType fromId(int id){
        return switch (id){
            case 1 -> new TypeIPv4();
            case 2 -> new TypeIPv6();
            case 3 -> new TypeHTTPStatus();
            case 4 -> new TypeDatetime();
            default -> new TypeEdgeResponse();
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
