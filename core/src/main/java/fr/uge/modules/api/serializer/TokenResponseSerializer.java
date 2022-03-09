package fr.uge.modules.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.linking.token.type.TokenType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class TokenResponseSerializer extends StdSerializer<TokenResponseSerializer.TokensResponse> {
    @JsonSerialize(using = TokenResponseSerializer.class)
    public record TokensResponse(List<LogEntity> logs, String error) {
    }

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    public TokenResponseSerializer(){
        this(null);
    }

    public TokenResponseSerializer(Class<TokensResponse> clazz){
        super(clazz);
    }

    @Override
    public void serialize(TokensResponse tokensResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeArrayFieldStart("logs");
        for(LogEntity logResponse : tokensResponse.logs){
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", logResponse.id);
            jsonGenerator.writeStringField("content", logResponse.rawLog.log);
            jsonGenerator.writeStringField("datetime", formatter.format(logResponse.datetime));
            jsonGenerator.writeArrayFieldStart("tokens");
            for(TokenEntity tokenEntity : logResponse.tokens){
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("token_type", TokenType.fromId(tokenEntity.idtokentype).getName());
                jsonGenerator.writeStringField("value", tokenEntity.value);
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();

        var error = tokensResponse.error;
        if(Objects.isNull(error)) {
            jsonGenerator.writeStringField("error", "");
        } else {
            jsonGenerator.writeStringField("error", tokensResponse.error);
        }
        jsonGenerator.writeEndObject();

    }
}
