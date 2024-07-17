package ru.terentyev.TaskManager.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class StringToIntegerDeserializer extends JsonDeserializer<long[]> {

    @Override
    public long[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.isTextual()) {
            return new long[]{Long.parseLong(node.asText())};
        } else if (node.isNumber()) {
            return new long[]{node.longValue()};
        } else {
            throw new IOException("Invalid value for integer: " + node.toString());
        }
    }
}