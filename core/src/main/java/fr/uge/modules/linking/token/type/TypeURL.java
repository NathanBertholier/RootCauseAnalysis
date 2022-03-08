package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.TokensLink;
import fr.uge.modules.linking.strategy.AverageStrategy;

import java.util.Collection;
import java.util.List;

/**
 * TypeURL TokenType
 * The REGEX matches on any string starting with "http://" or "https://"
 */
public class TypeURL implements TokenType{

    private static final String NAME = "url";
    private static final String REGEX = "https?://[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&\\/\\/=]*)";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getRegex() { return REGEX; }

    @Override
    public Integer getTokenTypeId() {
        return TokenTypeId.ID_URL.getId();
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
        if(listTokensLeft.isEmpty() || listTokensRight.isEmpty()) {
            return TokensLink.withoutStrategy(50);
        }

        String regex = "^(http[s]?://www\\.|http[s]?://|www\\.)";

        var computations = listTokensLeft.stream().map(token -> token.getValue().replaceFirst(regex, ""))
                .map(tokenL -> listTokensRight.stream()
                        .map(token -> token.getValue().replaceFirst(regex, "") )
                        .map(tokenR -> {
                            if ( tokenL.equals( tokenR ) ) {
                                return new Computation(this, tokenL, tokenR, 100d);
                            }

                            var arrayL = tokenL.split( "/" );
                            var arrayR = tokenR.split( "/" );
                            double count = 0;
                            for ( int i =0; i < arrayL.length-1; i++ ) {
                                if(i<= arrayR.length-1){
                                    if ( arrayL[i].equals( arrayR[i] ) ) {
                                        count++;
                                    }
                                    else {
                                        break;
                                    }
                                }
                            }
                            return new Computation(this, tokenL, tokenR, (count / arrayL.length) * 100);
                        }).toList())
                .flatMap(Collection::stream)
                .toList();
        return new TokensLink(computations, new AverageStrategy());
    }

}
