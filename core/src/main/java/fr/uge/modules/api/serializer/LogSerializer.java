package fr.uge.modules.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.report.GenericReport;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class LogSerializer extends StdSerializer<LogEntity> {
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    public LogSerializer(){
        this(null);
    }

    public LogSerializer(Class<LogEntity> report){
        super(report);
    }

    @Override
    public void serialize(LogEntity log, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", log.id);
        jsonGenerator.writeStringField("content", log.rawLog.log);
        jsonGenerator.writeStringField("datetime", formatter.format(log.datetime));
        jsonGenerator.writeEndObject();
    }
}
