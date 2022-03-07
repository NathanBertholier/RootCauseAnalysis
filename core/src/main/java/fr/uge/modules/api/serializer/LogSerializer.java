package fr.uge.modules.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.uge.modules.api.model.entities.LogResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class LogSerializer extends StdSerializer<LogResponse> {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    public LogSerializer(){
        this(null);
    }

    public LogSerializer(Class<LogResponse> report){
        super(report);
    }

    @Override
    public void serialize(LogResponse log, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        var logInside = log.logEntity();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", logInside.id);
        jsonGenerator.writeStringField("content", logInside.rawLog.log);
        jsonGenerator.writeStringField("datetime", formatter.format(logInside.datetime));
        jsonGenerator.writeEndObject();
    }
}
