package cn.nukkit.server.network.minecraft.util;

import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.server.nbt.util.VarInt;
import cn.nukkit.server.network.minecraft.data.MetadataConstants;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.buffer.ByteBuf;

import java.util.Objects;
import java.util.Optional;

public class MetadataDictionary {
    private static final MetadataConstants VALUES[] = MetadataConstants.values();
    private final TIntObjectMap<Object> typeMap = new TIntObjectHashMap<>();

    public void putAll(MetadataDictionary dictionary) {
        Preconditions.checkNotNull(dictionary, "dictionary");
        typeMap.putAll(dictionary.typeMap);
    }

    public Optional get(MetadataConstants type) {
        return Optional.ofNullable(typeMap.get(type.ordinal()));
    }

    public void put(MetadataConstants type, Object o) {
        Preconditions.checkNotNull(o, "o");
        Preconditions.checkArgument(isAcceptable(o), "object cannot be serialized");

        typeMap.put(type.ordinal(), o);
    }

    public void writeTo(ByteBuf buf) {
        VarInt.writeUnsignedInt(buf, typeMap.size());
        typeMap.forEachEntry((i, o) -> {
            serialize(buf, i, o);
            return true;
        });
    }

    private static boolean isAcceptable(Object o) {
        return o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Float || o
                instanceof String || o instanceof Vector3i || o instanceof Long || o instanceof ItemInstance;
    }

    public static MetadataConstants.Type getType(Object o) {
        Preconditions.checkNotNull(o, "o");
        if (o instanceof Byte) {
            return MetadataConstants.Type.BYTE;
        } else if (o instanceof Short) {
            return MetadataConstants.Type.SHORT;
        } else if (o instanceof Integer) {
            return MetadataConstants.Type.INT;
        } else if (o instanceof Float) {
            return MetadataConstants.Type.FLOAT;
        } else if (o instanceof String) {
            return MetadataConstants.Type.STRING;
        } else if (o instanceof ItemInstance) {
            return MetadataConstants.Type.ITEM;
        } else if (o instanceof Vector3i) {
            return MetadataConstants.Type.VECTOR3I;
        } else if (o instanceof Long) {
            return MetadataConstants.Type.LONG;
        } else if (o instanceof Vector3f) {
            return MetadataConstants.Type.VECTOR3F;
        }
        throw new IllegalArgumentException("Unsupported type" + o.getClass().getName());
    }

    public static MetadataDictionary deserialize(ByteBuf buf) {
        MetadataDictionary dictionary = new MetadataDictionary();
        int count = VarInt.readUnsignedInt(buf);
        for (int i = 0; i < count; i++) {
            int idx = VarInt.readUnsignedInt(buf);
            MetadataConstants.Type type = MetadataConstants.Type.values()[VarInt.readUnsignedInt(buf)];

            dictionary.typeMap.put(idx, type.read(buf));
        }
        return dictionary;
    }

    private static void serialize(ByteBuf buf, int idx, Object o) {
        Preconditions.checkNotNull(buf, "buf");
        Preconditions.checkNotNull(o, "o");

        MetadataConstants.Type type = getType(o);
        if (type == null) {
            throw new IllegalArgumentException("Unsupported type " + o.getClass().getName());
        }

        VarInt.writeUnsignedInt(buf, idx);
        VarInt.writeUnsignedInt(buf, type.ordinal());
        type.write(buf, o);
    }

    @Override
    public String toString() {
        return typeMap.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetadataDictionary that = (MetadataDictionary) o;
        return java.util.Objects.equals(typeMap, that.typeMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeMap);
    }
}
