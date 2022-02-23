package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

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
    public float computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        if ( tokenLeft.isEmpty() || tokenRight.isEmpty() ) return 0;
        float proximity = 0;
        for (TokenEntity tokenL : tokenLeft) {
            var firstLeftChar = tokenL.getValue().substring(0,1);
            for ( TokenEntity tokenR : tokenRight ) {
                var firstRightChar = tokenR.getValue().substring(0,1);
                if ( tokenL.getValue().equals( tokenR.getValue() ) ) {
                    return 100;
                }
                else if ( firstLeftChar.equals( firstRightChar ) ) {
                    proximity = 95;
                }
                else if ( proximity < 90 && ( firstLeftChar.equals( "4" ) || firstLeftChar.equals( "5" ) ) && ( firstRightChar.equals( "4" ) || firstRightChar.equals( "5" ) ) ) {
                    proximity = 90;
                }
                else if ( proximity < 25 && ( firstLeftChar.equals( "2" ) || firstLeftChar.equals( "3" ) ) && ( firstRightChar.equals( "4" ) || firstRightChar.equals( "5" ) ) ) {
                    proximity = 25;
                }
            }
        }

        return proximity;
    }
}
