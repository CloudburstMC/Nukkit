package cn.nukkit.entity.data;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.EnumMap;
import java.util.Objects;
import java.util.function.BiConsumer;

import static cn.nukkit.entity.data.EntityData.FLAGS;
import static cn.nukkit.entity.data.EntityData.FLAGS_EXTENDED;

public class EntityDataMap {
    private final EnumMap<EntityData, Object> map = new EnumMap<>(EntityData.class);

    public byte getByte(EntityData key) {
        return getByte(key, (byte) 0);
    }

    public byte getByte(EntityData key, byte defaultValue) {
        return get(key, defaultValue);
    }

    public EntityDataMap putByte(EntityData key, int value) {
        return put(key, (byte) value);
    }

    public short getShort(EntityData key) {
        return getShort(key, (short) 0);
    }

    public short getShort(EntityData key, short defaultValue) {
        return get(key, defaultValue);
    }

    public EntityDataMap putShort(EntityData key, int value) {
        return put(key, (short) value);
    }

    public int getInt(EntityData key) {
        return getInt(key, 0);
    }

    public int getInt(EntityData key, int defaultValue) {
        return get(key, defaultValue);
    }

    public EntityDataMap putInt(EntityData key, int value) {
        return put(key, value);
    }

    public float getFloat(EntityData key) {
        return getFloat(key, 0f);
    }

    public float getFloat(EntityData key, float defaultValue) {
        return get(key, defaultValue);
    }

    public EntityDataMap putFloat(EntityData key, float value) {
        return put(key, value);
    }

    public String getString(EntityData key) {
        return getString(key, "");
    }

    public String getString(EntityData key, String defaultValue) {
        return get(key, defaultValue);
    }

    public EntityDataMap putString(EntityData key, String value) {
        return put(key, value);
    }

    public CompoundTag getTag(EntityData key) {
        return getTag(key, new CompoundTag());
    }

    public CompoundTag getTag(EntityData key, CompoundTag defaultValue) {
        return get(key, defaultValue);
    }

    public EntityDataMap putTag(EntityData key, CompoundTag value) {
        return put(key, value);
    }

    public BlockVector3 getPos(EntityData key) {
        return getPos(key, new BlockVector3());
    }

    public BlockVector3 getPos(EntityData key, BlockVector3 defaultValue) {
        return get(key, defaultValue);
    }

    public EntityDataMap putPos(EntityData key, BlockVector3 value) {
        return put(key, value);
    }

    public long getLong(EntityData key) {
        return getLong(key, 0);
    }

    public long getLong(EntityData key, long defaultValue) {
        return get(key, defaultValue);
    }

    public EntityDataMap putLong(EntityData key, long value) {
        return put(key, value);
    }

    public Vector3f getVector3f(EntityData key) {
        return getVector3f(key, new Vector3f());
    }

    public Vector3f getVector3f(EntityData key, Vector3f defaultValue) {
        return get(key, defaultValue);
    }

    public EntityDataMap putVector3f(EntityData key, Vector3f value) {
        return put(key, value);
    }

    public EntityFlags getFlags() {
        return (EntityFlags) this.map.get(FLAGS);
    }

    public EntityDataMap putFlags(EntityFlags flags) {
        Objects.requireNonNull(flags, "flags");
        this.map.put(FLAGS, flags);
        this.map.put(FLAGS_EXTENDED, flags);
        return this;
    }

    public boolean contains(EntityData key) {
        return this.map.containsKey(key);
    }

    public Object remove(EntityData key) {
        return this.map.remove(key);
    }

    public EntityDataType getType(EntityData key) {
        Object value = this.map.get(key);
        if (value == null) {
            return null;
        }
        return EntityDataType.from(value);
    }

    @SuppressWarnings("unchecked")
    private <T> T get(EntityData key, T defaultValue) {
        Objects.requireNonNull(key, "key");
        Object object = this.map.get(key);
        if (object == null || key == FLAGS || key == FLAGS_EXTENDED) {
            return defaultValue;
        }
        try {
            return (T) object;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public EntityDataMap put(EntityData key, Object value) {
        Objects.requireNonNull(key, "type");
        Objects.requireNonNull(value, "value");
        if (key == FLAGS || key == FLAGS_EXTENDED) {
            throw new IllegalArgumentException(key + " cannot be set using this method");
        }
        EntityDataType.from(value); // make sure the value is legal.
        this.map.put(key, value);
        return this;
    }

    public void putAll(EntityDataMap map) {
        this.map.putAll(map.map);
    }

    public int size() {
        return this.map.size();
    }

    public void forEach(BiConsumer<EntityData, Object> consumer) {
        this.map.forEach(consumer);
    }

    public void clear() {
        this.map.clear();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public String toString() {
        return "EntityDataMap(" +
                "map=" + map +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityDataMap that = (EntityDataMap) o;

        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
