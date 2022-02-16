package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.TokenModel;

import java.util.Objects;

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

    float computeProximity(TokenModel t1, TokenModel t2);

    enum TokenTypeId {
        ID_IPV4(1),
        ID_IPV6(2),
        ID_STATUS(3),
        ID_DATETIME(4),
        ID_EDGERESPONSE(5);

        private final int id;

        TokenTypeId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
