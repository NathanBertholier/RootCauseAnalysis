package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.Link;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

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
    public Link computeProximity(List<TokenEntity> listTokensLeft, List<TokenEntity> listTokensRight) {
        var type = new TypeEdgeResponse();
        if (listTokensLeft.isEmpty() || listTokensRight.isEmpty()) return Link.emptyLink(0);

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
        return new Link(computations, computations.stream().mapToDouble(Computation::proximity).sum() / computations.size());
    }
}
