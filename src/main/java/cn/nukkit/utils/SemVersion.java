package cn.nukkit.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

@JsonAdapter(SemVersion.Adapter.class)
public final class SemVersion {
    private int[] version;

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

    public static class Adapter extends TypeAdapter<SemVersion> {

        @Override
        public void write(JsonWriter out, SemVersion value) throws IOException {
            int[] version = value.version;
            out.beginArray().value(version[0]).value(version[1]).value(version[2]).endArray();
        }

        @Override
        public SemVersion read(JsonReader in) throws IOException {
            in.beginArray();
            int major = in.nextInt();
            int minor = in.nextInt();
            int patch = in.nextInt();
            in.endArray();

            return new SemVersion(major, minor, patch);
        }
    }
}
