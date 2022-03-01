package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.Link;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Link computeProximity(List<TokenEntity> listTokensLeft, List<TokenEntity> listTokensRight) {
        if (listTokensLeft.isEmpty() || listTokensRight.isEmpty()) return Link.emptyLink(0);
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

        return new Link(computations, computations.stream().mapToDouble(Computation::proximity).sum() / computations.size());
    }

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
