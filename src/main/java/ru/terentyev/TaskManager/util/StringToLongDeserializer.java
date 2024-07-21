package ru.terentyev.TaskManager.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class StringToLongDeserializer extends JsonDeserializer<Long[]> {

    @Override
    public Long[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.isTextual()) {
            return new Long[]{Long.parseLong(node.asText())};
        } else if (node.isNumber()) {
            return new Long[]{node.longValue()};
        } else {
            throw new IOException("Invalid value for integer: " + node.toString());
        }
    }
}