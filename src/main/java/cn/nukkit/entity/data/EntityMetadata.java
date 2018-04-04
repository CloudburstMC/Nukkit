package cn.nukkit.entity.data;

import cn.nukkit.block.BlockAir;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.Vector3;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityMetadata {

    private final Map<Integer, EntityData> map = new HashMap<>();

    public EntityData get(int id) {
        return this.getOrDefault(id, null);
    }

    public EntityData getOrDefault(int id, EntityData defaultValue) {
        try {
            return this.map.getOrDefault(id, defaultValue).setId(id);
        } catch (Exception e) {
            if (defaultValue != null) {
                return defaultValue.setId(id);
            }
            return null;
        }
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

    public Item getSlot(int id) {
        return (Item) this.getOrDefault(id, new SlotEntityData(id, new ItemBlock(new BlockAir()))).getData();
    }

    public String getString(int id) {
        return (String) this.getOrDefault(id, new StringEntityData(id, "")).getData();
    }

    public Vector3 getPosition(int id) {
        return (Vector3) this.getOrDefault(id, new IntPositionEntityData(id, new Vector3())).getData();
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

    public EntityMetadata putSlot(int id, int blockId, int meta, int count) {
        return this.put(new SlotEntityData(id, blockId, (byte) meta, count));
    }

    public EntityMetadata putSlot(int id, Item value) {
        return this.put(new SlotEntityData(id, value));
    }

    public EntityMetadata putString(int id, String value) {
        return this.put(new StringEntityData(id, value));
    }

    public Map<Integer, EntityData> getMap() {
        return new HashMap<>(map);
    }
}
