package cn.nukkit.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

@JsonSerialize(using = SemVersion.Serializer.class)
@JsonDeserialize(using = SemVersion.Deserializer.class)
public final class SemVersion {
    private final int[] version;

    public SemVersion(int major, int minor, int patch) {
        this.version = new int[]{major, minor, patch};
    }

    public int getMajor() {
        return this.version[0];
    }

    public int getMinor() {
        return this.version[1];
    }

    public int getPatch() {
        return this.version[2];
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", version[0], version[1], version[2]);
    }

    static class Serializer extends StdSerializer<SemVersion> {

        protected Serializer() {
            super(SemVersion.class);
        }

        @Override
        public void serialize(SemVersion value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeArray(value.version, 0, 3);
        }
    }

    static class Deserializer extends StdDeserializer<SemVersion> {

        protected Deserializer() {
            super(SemVersion.class);
        }

        @Override
        public SemVersion deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            int[] version = p.readValueAs(int[].class);
            return new SemVersion(version[0], version[1], version[2]);
        }
    }
}
