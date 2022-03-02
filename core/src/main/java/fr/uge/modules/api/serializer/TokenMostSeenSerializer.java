package fr.uge.modules.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.uge.modules.api.model.TokensMostSeen;

import java.io.IOException;

public class TokenMostSeenSerializer extends StdSerializer<TokensMostSeen> {
    public TokenMostSeenSerializer(){
        this(null);
    }

    public TokenMostSeenSerializer(Class<TokensMostSeen> token){
        super(token);
    }

    @Override
    public void serialize(TokensMostSeen tokensMostSeen, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeArrayFieldStart("token_values");
        jsonGenerator.writeEndArray();
    }
}