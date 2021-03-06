package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.TokensLink;
import fr.uge.modules.linking.strategy.AverageStrategy;

import java.util.Collection;
import java.util.List;

/**
 * TypeEdgeResponse TokenType
 * REGEX is the keywords of the token
 * ERRORS contains the keywords considered as errors
 */
public class TypeEdgeResponse implements TokenType {
    private static final String NAME = "edgeresponse";
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
    public Integer getTokenTypeId() {
        return TokenTypeId.ID_EDGERESPONSE.getId();
    }

    /**
     * Method inherited from TokenType interface
     * Compute the proximity between each token of the same time for two logs
     * @param listTokensLeft Tokens from log 1
     * @param listTokensRight Tokens from log 2
     * @return a TokensLink object containing all the computations
     */
    @Override
    public TokensLink computeProximity(List<TokenEntity> listTokensLeft, List<TokenEntity> listTokensRight) {
        var type = new TypeEdgeResponse();
        if (listTokensLeft.isEmpty() || listTokensRight.isEmpty()) return TokensLink.withoutStrategy(50);

        var computations = listTokensLeft.stream()
                .map(TokenEntity::getValue)
                .map(leftValue -> listTokensRight.stream()
                        .map(TokenEntity::getValue)
                        .map(rightValue -> {
                            if (leftValue.equals(rightValue)) {
                                return new Computation(type, leftValue, rightValue, 100d);
                            }
                            else if (((ERRORS.contains(leftValue)
                                    && ERRORS.contains(rightValue)) || (!ERRORS.contains(leftValue)
                                    && !ERRORS.contains(rightValue)))) {
                                return new Computation(type, leftValue, rightValue, 95d);
                            }
                            else if (!ERRORS.contains(leftValue) && ERRORS.contains(rightValue)) {
                                return new Computation(type, leftValue, rightValue, 25d);
                            }
                            else return new Computation(type, leftValue, rightValue, 0d);
                        }).toList()
                ).flatMap(Collection::stream).toList();
        return new TokensLink(computations, new AverageStrategy());
    }
}
