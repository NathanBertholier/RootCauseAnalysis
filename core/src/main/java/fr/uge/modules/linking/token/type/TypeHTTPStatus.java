package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.TokensLink;
import fr.uge.modules.linking.strategy.AverageStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * TypeHTTPStatus TokenType
 * The REGEX matches on most common HTTP status
 */
public class TypeHTTPStatus implements TokenType{

    private static final String NAME = "status";
    private static final String REGEX = "([1-5][0-5][0-9])";

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
        return TokenTypeId.ID_STATUS.getId();
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
        if (listTokensLeft.isEmpty() || listTokensRight.isEmpty()) return TokensLink.withoutStrategy(0);
        var type = new TypeHTTPStatus();

        List<Computation> computations = new ArrayList<>();

        for (TokenEntity tokenLeft : listTokensLeft) {
            var tokenLeftValue = tokenLeft.value;
            for (TokenEntity tokenRight : listTokensRight) {
                var tokenRightValue = tokenRight.value;
                var proximity = fromValues(tokenLeftValue, tokenRightValue);
                computations.add(new Computation(type, tokenLeftValue, tokenRightValue, proximity));
            }
        }

        return new TokensLink(computations, new AverageStrategy());
    }

    /**
     * Computes the proximity between two HTTP Status
     * @param left left HTTP Status
     * @param right right HHTP Status
     * @return the double result of the computation
     */
    private static double fromValues(String left, String right){
        if (left.equals(right)) {
            return 100;
        }
        var firstLeftChar = left.substring(0,1);
        var firstRightChar = right.substring(0,1);

        if (firstLeftChar.equals(firstRightChar)) {
            return 95;
        }

        else if ((firstLeftChar.equals("4") || firstLeftChar.equals("5"))
                && ( firstRightChar.equals("4") || firstRightChar.equals("5"))) {
            return 90;
        }
        else if ((firstLeftChar.equals("2") || firstLeftChar.equals("3"))
                && ( firstRightChar.equals("4") || firstRightChar.equals("5"))) {
            return 25;
        } else return 0;
    }
}
