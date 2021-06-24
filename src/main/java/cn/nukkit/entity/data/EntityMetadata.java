package cn.nukkit.entity.data;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class EntityMetadata {

    private final Map<Integer, EntityData> map = new HashMap<>();

    public EntityData get(int id) {
        return this.getOrDefault(id, null);
    }

    @PowerNukkitDifference(info = "Reduce a lot of hidden NullPointerExceptions", since = "1.3.1.2-PN")
    public EntityData getOrDefault(int id, EntityData defaultValue) {
        EntityData data = this.map.getOrDefault(id, defaultValue);
        if (data == null) {
            return null;
        }
        data.setId(id);
        return data;
    }

    public boolean exists(int id) {
        return this.map.containsKey(id);
    }

    public EntityMetadata put(EntityData data) {
        this.map.put(data.getId(), data);
        return this;
    }

    public int getByte(int id) {
        return (int) this.getOrDefault(id, new ByteEntityData(id, 0)).getData() & 0xff;
    }

    public int getShort(int id) {
        return (int) this.getOrDefault(id, new ShortEntityData(id, 0)).getData();
    }

    public int getInt(int id) {
        return (int) this.getOrDefault(id, new IntEntityData(id, 0)).getData();
    }

    public long getLong(int id) {
        return (Long) this.getOrDefault(id, new LongEntityData(id, 0)).getData();
    }

    public float getFloat(int id) {
        return (float) this.getOrDefault(id, new FloatEntityData(id, 0)).getData();
    }

    public boolean getBoolean(int id) {
        return this.getByte(id) == 1;
    }

    public CompoundTag getNBT(int id) {
        return (CompoundTag) this.getOrDefault(id, new NBTEntityData(id, new CompoundTag())).getData();
    }

    public String getString(int id) {
        return (String) this.getOrDefault(id, new StringEntityData(id, "")).getData();
    }

    public Vector3 getPosition(int id) {
        return (Vector3) this.getOrDefault(id, new IntPositionEntityData(id, new Vector3())).getData();
    }

    public Vector3f getFloatPosition(int id) {
        return (Vector3f) this.getOrDefault(id, new Vector3fEntityData(id, new Vector3f())).getData();
    }

    public EntityMetadata putByte(int id, int value) {
        return this.put(new ByteEntityData(id, value));
    }

    public EntityMetadata putShort(int id, int value) {
        return this.put(new ShortEntityData(id, value));
    }

    public EntityMetadata putInt(int id, int value) {
        return this.put(new IntEntityData(id, value));
    }

    public EntityMetadata putLong(int id, long value) {
        return this.put(new LongEntityData(id, value));
    }

    public EntityMetadata putFloat(int id, float value) {
        return this.put(new FloatEntityData(id, value));
    }

    public EntityMetadata putBoolean(int id, boolean value) {
        return this.putByte(id, value ? 1 : 0);
    }

    public EntityMetadata putNBT(int id, CompoundTag tag) {
        return this.put(new NBTEntityData(id, tag));
    }

    @Deprecated
    public EntityMetadata putSlot(int id, Item value) {
        return this.put(new NBTEntityData(id, value.getNamedTag()));
    }

    public EntityMetadata putString(int id, String value) {
        return this.put(new StringEntityData(id, value));
    }

    public Map<Integer, EntityData> getMap() {
        return new HashMap<>(map);
    }
}
