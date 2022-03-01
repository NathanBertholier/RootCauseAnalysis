package fr.uge.modules.linking;

import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.LinkResponse;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.linking.token.type.*;

import java.util.*;
import java.util.logging.Logger;

public class ReportLinking {

    private final HashMap<Integer, TokenType> tokensType = new HashMap<>();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public ReportLinking() {
        this.addInTokensType(TokenType.TokenTypeId.ID_IPV4.getId(), new TypeIPv4());
        this.addInTokensType(TokenType.TokenTypeId.ID_IPV6.getId(), new TypeIPv6());
        this.addInTokensType(TokenType.TokenTypeId.ID_DATETIME.getId(), new TypeDatetime());
        this.addInTokensType(TokenType.TokenTypeId.ID_EDGERESPONSE.getId(), new TypeEdgeResponse());
        this.addInTokensType(TokenType.TokenTypeId.ID_STATUS.getId(), new TypeHTTPStatus());
    }

    private void addInTokensType(Integer id, TokenType tokenType) {
        tokensType.compute(id, (k,v) -> tokenType);
    }

    private void fillHashmap(List<TokenEntity> tokens, HashMap<Integer, List<TokenEntity>> tokensToFill) {
        tokens.forEach(token -> tokensToFill
                .computeIfAbsent(token.getIdtokentype(), ArrayList::new)
                .add(token)
        );
    }

    public SortedSet<LinkResponse> computeProximityTree(LogEntity logTarget, List<LogEntity> logWithinDelta, ReportParameter rp){
        TreeSet<LinkResponse> redBlack = new TreeSet<>(Comparator.comparingDouble(linkResponse -> linkResponse.links().proximity()));
        var targetDatetime = logTarget.datetime;

        HashMap<Integer, List<TokenEntity>> tokenTarget = new HashMap<>();
        fillHashmap(logTarget.tokens, tokenTarget);

        var delta = rp.delta();

        HashMap<Integer, List<TokenEntity>> tokenToLink = new HashMap<>();
        this.tokensType.forEach((id,v) -> tokenToLink.computeIfAbsent(id, k -> new ArrayList<>()));

        logWithinDelta.forEach(log -> {
            Computation dateTimeCommputation = TypeDatetime.computeDateTimeProximity(log.datetime, targetDatetime, delta);
            log.getTokens().forEach(token -> tokenToLink.get(token.getIdtokentype()).add(token));

            tokenTarget.keySet().stream()
                    .map(tokentypeid -> {
                        var tokenType = TokenType.fromId(tokentypeid);
                        var target = tokenTarget.get(tokentypeid);
                        var toLink = tokenToLink.get(tokentypeid);
                        return tokenType.computeProximity(target, toLink);
                    })
                    .map(link -> new LinkResponse(logTarget, log, link))
                    .forEach(linkResponse -> {
                        var proximity = linkResponse.links().proximity();
                        if(proximity > rp.proximity_limit()){
                            if(redBlack.size() == rp.network_size()){
                                redBlack.pollFirst();
                                redBlack.add(linkResponse);
                            } else redBlack.add(linkResponse);
                        }
                    });
        });

        System.out.println("RedBlack: " + redBlack);
        return redBlack.descendingSet();
    }

}
