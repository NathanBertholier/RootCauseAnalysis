package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.TokensLink;
import fr.uge.modules.linking.strategy.AverageStrategy;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

/**
 * TypeResource TokenType
 * The REGEX matches on string starting with "/"
 */
public class TypeResource implements TokenType{

    private static final String NAME = "resource";
    private static final String REGEX = "^(/(\\S)+)+";

    @Override
    public String getName() { return NAME; }

    @Override
    public String getRegex() { return REGEX; }

    @Override
    public Integer getTokenTypeId() { return TokenTypeId.ID_RESOURCE.getId(); }

    /**
     * Method inherited from TokenType interface
     * Compute the proximity between each token of the same time for two logs
     * @param listTokensLeft Tokens from log 1
     * @param listTokensRight Tokens from log 2
     * @return a TokensLink object containing all the computations
     */
    @Override
    public TokensLink computeProximity(List<TokenEntity> listTokensLeft, List<TokenEntity> listTokensRight) {
        if(listTokensLeft.isEmpty() || listTokensRight.isEmpty()) {
            return TokensLink.withoutStrategy(50);
        }
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2);
        format.setRoundingMode(RoundingMode.FLOOR);
        var computations = listTokensLeft.stream().map(TokenEntity::getValue)
                .map(tokenL -> listTokensRight.stream()
                        .map(TokenEntity::getValue)
                        .map(tokenR -> {
                            if ( tokenL.equals( tokenR ) ) {
                                return new Computation(this, tokenL, tokenR, 100d);
                            }
                            var startsWith = "/";
                            var arrayL = tokenL.split(startsWith);
                            var arrayR = tokenR.split(startsWith);
                            double count = 0;
                            for ( int i = 1; i < arrayL.length - 1; i++ ) {
                                if(i<= arrayR.length-1){
                                    if ( arrayL[i].equals( arrayR[i] ) ) {
                                        count++;
                                    } else {
                                        break;
                                    }
                                }
                            }
                            var res = Double.parseDouble(format.format((count / Math.min(arrayL.length, arrayR.length)) * 100).replace(",", "."));
                            return new Computation(this, tokenL, tokenR, res);
                        } )
                        .toList())
                .flatMap(Collection::stream)
                .toList();
        return new TokensLink(computations, new AverageStrategy());
    }

}
