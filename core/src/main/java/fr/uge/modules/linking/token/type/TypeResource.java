package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

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
    public double computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        if(tokenLeft.isEmpty() || tokenRight.isEmpty()) {
            return 50;
        }
        return tokenLeft.stream().map(TokenEntity::getValue)
                .mapToDouble(tokenL -> tokenRight.stream()
                        .map(TokenEntity::getValue)
                        .mapToDouble(tokenR -> {
                            if ( tokenL.equals( tokenR ) ) {
                                return 100;
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
                            return (count / arrayL.length) * 100;
                        } ).reduce( 0,Double::max)).reduce(0, Double::sum) / tokenLeft.size();

    }

}
