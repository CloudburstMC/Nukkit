package cn.nukkit.level.persistence;

import cn.nukkit.nbt.tag.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

public interface PersistentDataType<T> {

    PersistentDataType<Boolean> BOOLEAN = new PrimitiveDataType<>(Boolean.class, ByteTag.class, val -> new ByteTag(null, val ? 1 : 0), tag -> tag.parseValue() == 1);
    PersistentDataType<Byte> BYTE = new PrimitiveDataType<>(Byte.class, ByteTag.class, val -> new ByteTag(null, val), tag -> tag.parseValue().byteValue());
    PersistentDataType<Integer> INT = new PrimitiveDataType<>(Integer.class, IntTag.class, val -> new IntTag(null, val), IntTag::parseValue);
    PersistentDataType<Double> DOUBLE = new PrimitiveDataType<>(Double.class, DoubleTag.class, val -> new DoubleTag(null, val), DoubleTag::parseValue);
    PersistentDataType<Float> FLOAT = new PrimitiveDataType<>(Float.class, FloatTag.class, val -> new FloatTag(null, val), FloatTag::parseValue);
    PersistentDataType<String> STRING = new PrimitiveDataType<>(String.class, StringTag.class, val -> new StringTag(null, val), StringTag::parseValue);

    Class<T> getImplementation();

    Tag serialize(T value);

    T deserialize(Tag tag);

    boolean validate(Tag tag);

    @Getter
    @AllArgsConstructor
    class PrimitiveDataType<T, I extends Tag> implements PersistentDataType<T> {
        private final Class<T> implementation;
        private final Class<I> tagClass;
        private final Function<T, I> serializer;
        private final Function<I, T> deserializer;

        @Override
        public Tag serialize(T value) {
            return this.serializer.apply(value);
        }

        @Override
        public T deserialize(Tag tag) {
            return this.deserializer.apply((I) tag);
        }

        @Override
        public boolean validate(Tag tag) {
            return this.tagClass.isAssignableFrom(tag.getClass());
        }
    }
}
