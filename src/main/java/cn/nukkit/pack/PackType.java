package cn.nukkit.pack;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

@JsonAdapter(PackType.Adapter.class)
public enum PackType {
    INVALID,
    RESOURCES,
    DATA,
    PLUGIN,
    CLIENT_DATA,
    INTERFACE,
    MANDATORY,
    WORLD_TEMPLATE;

    public static class Adapter extends TypeAdapter<PackType> {

        @Override
        public void write(JsonWriter out, PackType value) throws IOException {
            out.value(value.name().toLowerCase());
        }

        @Override
        public PackType read(JsonReader in) throws IOException {
            return valueOf(in.nextString().toUpperCase());
        }
    }
}
