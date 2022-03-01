package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.Links;

import java.util.Collection;
import java.util.List;

public class TypeResource implements TokenType{

    private static final String NAME = "resource";
    private static final String REGEX = "^(/(\\S)+)+";

    @Override
    public String getName() { return NAME; }

    @Override
    public String getRegex() { return REGEX; }

    @Override
    public Integer getTokenTypeId() { return TokenTypeId.ID_RESOURCE.getId(); }

    @Override
    public Links computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        if(tokenLeft.isEmpty() || tokenRight.isEmpty()) {
            return Links.emptyLink(50);
        }
        var computations = tokenLeft.stream().map(TokenEntity::getValue)
                .map(tokenL -> tokenRight.stream()
                        .map(TokenEntity::getValue)
                        .map(tokenR -> {
                            if ( tokenL.equals( tokenR ) ) {
                                return new Computation(this, tokenL, tokenR, 100d);
                            }
                            var startsWith = "/";
                            var arrayL = tokenL.split(startsWith);
                            var arrayR = tokenR.split(startsWith);
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
                        } )
                        .toList())
                .flatMap(Collection::stream)
                .toList();
        return new Links(computations, computations.stream().mapToDouble(Computation::proximity).sum() / computations.size());
    }

}
