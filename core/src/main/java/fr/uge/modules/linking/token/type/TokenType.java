package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.TokensLink;

import java.util.Objects;
import java.util.List;

/**
 * Interface representing the token types we will be considering
 */
public interface TokenType {

    String getName();
    String getRegex();
    Integer getTokenTypeId();

    /**
     * Calls the method of a TokenType in order to retrieve its TokenTypeID
     * @param word the word to match
     * @return the id of the TokenType
     */
    default int matcher(String word) {
        Objects.requireNonNull(word);
        if (word.matches(getRegex())) {
            return getTokenTypeId();
        }
        return -1;
    }

    TokensLink computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight);

    /**
     * Returns a TokenType from an integer representing its ID
     * @param id the id we want to match
     * @return the TokenType corresponding
     */
    static TokenType fromId(int id){
        return switch (id){
            case 1 -> new TypeIPv4();
            case 2 -> new TypeIPv6();
            case 3 -> new TypeHTTPStatus();
            case 4 -> new TypeDatetime();
            case 5 -> new TypeEdgeResponse();
            case 6 -> new TypeURL();
            case 7 -> new TypeResource();
            default -> throw new IllegalStateException("TokenType not recognized");
        };
    }

    /**
     * Enumeration representing the ID of the TokenTypes
     */
    enum TokenTypeId {
        ID_IPV4(1),
        ID_IPV6(2),
        ID_STATUS(3),
        ID_DATETIME(4),
        ID_EDGERESPONSE(5),
        ID_URL(6),
        ID_RESOURCE(7);

        private final int id;
        TokenTypeId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }
    }
}
