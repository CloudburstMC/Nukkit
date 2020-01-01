package cn.nukkit.entity.data;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.Objects;

public enum EntityDataType {
    BYTE(Byte.class),
    SHORT(Short.class),
    INT(Integer.class),
    FLOAT(Float.class),
    STRING(String.class),
    NBT(CompoundTag.class),
    POS(BlockVector3.class),
    LONG(Long.class),
    VECTOR3F(Vector3f.class);

    private static final EntityDataType[] VALUES = values();
    private static final ImmutableMap<Class<?>, EntityDataType> TYPE_MAP;

    static {
        ImmutableMap.Builder<Class<?>, EntityDataType> builder = ImmutableMap.builder();
        for (EntityDataType type : VALUES) {
            builder.put(type.typeClass, type);
        }
        builder.put(EntityFlags.class, LONG);
        TYPE_MAP = builder.build();
    }

    private final Class<?> typeClass;

    EntityDataType(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public static EntityDataType from(Object value) {
        Objects.requireNonNull(value, "value");
        EntityDataType type = TYPE_MAP.get(value.getClass());
        if (type == null) {
            throw new IllegalArgumentException("Object is not related to any EntityDataType");
        }
        return type;
    }

    public static EntityDataType from(int id) {
        Preconditions.checkElementIndex(id, VALUES.length);
        return VALUES[id];
    }
}
