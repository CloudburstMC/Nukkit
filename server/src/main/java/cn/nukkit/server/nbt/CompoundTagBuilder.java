package cn.nukkit.server.nbt;

import cn.nukkit.server.nbt.tag.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
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

    public CompoundTagBuilder byteTag(String name, byte value) {
        return tag(new ByteTag(name, value));
    }

    public CompoundTagBuilder byteArrayTag(String name, byte[] value) {
        return tag(new ByteArrayTag(name, value));
    }

    public CompoundTagBuilder doubleTag(String name, double value) {
        return tag(new DoubleTag(name, value));
    }

    public CompoundTagBuilder floatTag(String name, float value) {
        return tag(new FloatTag(name, value));
    }

    public CompoundTagBuilder intArrayTag(String name, int[] value) {
        return tag(new IntArrayTag(name, value));
    }

    public CompoundTagBuilder intTag(String name, int value) {
        return tag(new IntTag(name, value));
    }

    public CompoundTagBuilder longTag(String name, long value) {
        return tag(new LongTag(name, value));
    }

    public CompoundTagBuilder shortTag(String name, short value) {
        return tag(new ShortTag(name, value));
    }

    public CompoundTagBuilder stringTag(String name, String value) {
        return tag(new StringTag(name, value));
    }

    public <T extends Tag> CompoundTagBuilder listTag(String name, Class<T> tagClass, List<T> values) {
        return tag(new ListTag<>(name, tagClass, values));
    }

    public CompoundTag buildRootTag() {
        return new CompoundTag("", tagMap);
    }

    public CompoundTag build(String tagName) {
        return new CompoundTag(tagName, tagMap);
    }
}
