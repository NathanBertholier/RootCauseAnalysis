package fr.uge.modules.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.uge.modules.api.model.entities.TokenEntity;

import java.io.IOException;

public class TokenSerializer extends StdSerializer<TokenEntity> {
    public TokenSerializer(){
        this(null);
    }

    public TokenSerializer(Class<TokenEntity> clazz){
        super(clazz);
    }

    @Override
    public void serialize(TokenEntity token, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", token.token_type.name);
        jsonGenerator.writeStringField("value", token.value);
        jsonGenerator.writeEndObject();
    }
}
