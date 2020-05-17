package cn.nukkit.pack;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

@JsonSerialize(using = PackType.Serializer.class)
@JsonDeserialize(using = PackType.Deserializer.class)
public enum PackType {
    INVALID,
    RESOURCES,
    DATA,
    PLUGIN,
    CLIENT_DATA,
    INTERFACE,
    MANDATORY,
    WORLD_TEMPLATE;

    static class Serializer extends StdSerializer<PackType> {

        protected Serializer() {
            super(PackType.class);
        }

        @Override
        public void serialize(PackType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.name().toLowerCase());
        }
    }

    static class Deserializer extends StdDeserializer<PackType> {

        protected Deserializer() {
            super(PackType.class);
        }

        @Override
        public PackType deserialize(JsonParser p, DeserializationContext ctxt) {
            try {
                return valueOf(p.getValueAsString().toUpperCase());
            } catch (Exception e) {
                return null;
            }
        }
    }
}
