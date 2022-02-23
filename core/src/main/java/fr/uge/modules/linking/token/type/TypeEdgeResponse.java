package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.List;

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
    public Integer getTokenTypeId() {
        return TokenTypeId.ID_EDGERESPONSE.getId();
    }

    @Override
    public double computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        if (tokenLeft.isEmpty() || tokenRight.isEmpty()) return 50;

        return tokenLeft.stream()
                .map(TokenEntity::getValue)
                .mapToDouble(leftValue -> tokenRight.stream()
                        .map(TokenEntity::getValue)
                        .mapToDouble(rightValue -> {
                            if (leftValue.equals(rightValue)) {
                                return 100;
                            }
                            else if (((ERRORS.contains(leftValue)
                                    && ERRORS.contains(rightValue)) || (!ERRORS.contains(leftValue)
                                    && !ERRORS.contains(rightValue)))) {
                                return 95;
                            }
                            else if (!ERRORS.contains(leftValue) && ERRORS.contains(rightValue)) {
                                return 25;
                            }
                            return 0;
                        }).reduce(0, Double::max))
                .reduce(0, Double::sum) / tokenLeft.size();
    }
}
