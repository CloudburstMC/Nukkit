package cn.nukkit.server.nbt;

import cn.nukkit.server.nbt.tag.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompoundTagBuilder {
    private final Map<String, Tag<?>> tagMap = new HashMap<>();

    public static CompoundTagBuilder builder() {
        return new CompoundTagBuilder();
    }

    public static CompoundTagBuilder from(CompoundTag tag) {
        CompoundTagBuilder builder = new CompoundTagBuilder();
        builder.tagMap.putAll(tag.getValue());
        return builder;
    }

    public CompoundTagBuilder tag(Tag<?> tag) {
        tagMap.put(tag.getName(), tag);
        return this;
    }

    public CompoundTagBuilder tagByte (String name, byte value) {
        return tag(new ByteTag(name, value));
    }

    public CompoundTagBuilder tagByteArray (String name, byte [] value) {
        return tag(new ByteArrayTag(name, value));
    }

    public CompoundTagBuilder tagDouble (String name, double value) {
        return tag(new DoubleTag(name, value));
    }

    public CompoundTagBuilder tagFloat (String name, float value) {
        return tag(new FloatTag(name, value));
    }

    public CompoundTagBuilder tagIntArray (String name, int[] value) {
        return tag(new IntArrayTag(name, value));
    }

    public CompoundTagBuilder tagInt (String name, int value) {
        return tag(new IntTag(name, value));
    }

    public CompoundTagBuilder tagLong (String name, long value) {
        return tag(new LongTag(name, value));
    }

    public CompoundTagBuilder tagShort (String name, short value) {
        return tag(new ShortTag(name, value));
    }

    public CompoundTagBuilder tagString (String name, String value) {
        return tag(new StringTag(name, value));
    }

    public CompoundTagBuilder tagCompoundTag (String name, CompoundTag value) {
        tagMap.put(name, value);
        return this;
    }

    public CompoundTagBuilder tagListTag(String name, ListTag value) {
        tagMap.put(name, value);
        return this;
    }

    public CompoundTag buildRootTag() {
        return new CompoundTag("", tagMap);
    }

    public CompoundTag build(String tagName) {
        return new CompoundTag(tagName, tagMap);
    }
}
