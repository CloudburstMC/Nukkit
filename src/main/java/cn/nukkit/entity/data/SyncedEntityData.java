package cn.nukkit.entity.data;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.data.EntityData;
import com.nukkitx.protocol.bedrock.data.EntityDataMap;
import com.nukkitx.protocol.bedrock.data.EntityFlag;
import com.nukkitx.protocol.bedrock.data.EntityFlags;

import java.util.Objects;

public class SyncedEntityData {
    private final EntityDataMap data = new EntityDataMap();
    private final EntityDataMap dataChangeSet = new EntityDataMap();
    private final EntityFlags flags = new EntityFlags();
    private final SyncedEntityDataListener listener;

    public SyncedEntityData(SyncedEntityDataListener listener) {
        this.listener = listener;
    }

    public void update() {
        if (this.dataChangeSet.isEmpty()) {
            return;
        }

        this.listener.onDataChange(this.dataChangeSet);
        this.dataChangeSet.clear();
    }

    public void putAllIn(EntityDataMap map) {
        map.putAll(this.data);
    }

    public void putFlagsIn(EntityDataMap map) {
        map.putFlags(this.flags);
    }

    public boolean contains(EntityData data) {
        return this.data.containsKey(data);
    }

    public EntityData.Type getType(EntityData data) {
        return this.data.getType(data);
    }

    public int getInt(EntityData data) {
        return this.data.getInt(data);
    }

    public void setInt(EntityData data, int value) {
        int oldValue = getInt(data);
        if (oldValue != value) {
            this.data.putInt(data, value);
            this.dataChangeSet.putInt(data, value);
        }
    }

    public short getShort(EntityData data) {
        return this.data.getShort(data);
    }

    public void setShort(EntityData data, int value) {
        value = (short) value;
        int oldValue = getShort(data);
        if (oldValue != value) {
            this.data.putShort(data, value);
            this.dataChangeSet.putShort(data, value);
        }
    }

    public byte getByte(EntityData data) {
        return this.data.getByte(data);
    }

    public void setByte(EntityData data, int value) {
        value = (byte) value;
        int oldValue = getByte(data);
        if (oldValue != value) {
            this.data.putByte(data, value);
            this.dataChangeSet.putByte(data, value);
        }
    }

    public boolean getBoolean(EntityData data) {
        return getByte(data) != 0;
    }

    public void setBoolean(EntityData data, boolean value) {
        boolean oldValue = getBoolean(data);
        if (oldValue != value) {
            this.data.putByte(data, value ? 1 : 0);
            this.dataChangeSet.putByte(data, value ? 1 : 0);
        }
    }

    public long getLong(EntityData data) {
        return this.data.getLong(data);
    }

    public void setLong(EntityData data, long value) {
        long oldValue = getLong(data);
        if (oldValue != value) {
            this.data.putLong(data, value);
            this.dataChangeSet.putLong(data, value);
        }
    }

    public String getString(EntityData data) {
        return this.data.getString(data);
    }

    public void setString(EntityData data, String value) {
        if (value == null) value = "";
        String oldValue = getString(data);
        if (!Objects.equals(oldValue, value)) {
            this.data.putString(data, value);
            this.dataChangeSet.putString(data, value);
        }
    }

    public float getFloat(EntityData data) {
        return this.data.getFloat(data);
    }

    public void setFloat(EntityData data, float value) {
        float oldValue = getFloat(data);
        if (oldValue != value) {
            this.data.putFloat(data, value);
            this.dataChangeSet.putFloat(data, value);
        }
    }

    public CompoundTag getTag(EntityData data) {
        return this.data.getTag(data);
    }

    public void setTag(EntityData data, CompoundTag value) {
        if (value == null) value = CompoundTag.EMPTY;
        CompoundTag oldValue = getTag(data);
        if (!Objects.equals(oldValue, value)) {
            this.data.putTag(data, value);
            this.dataChangeSet.putTag(data, value);
        }
    }

    public Vector3i getVector3i(EntityData data) {
        return this.data.getPos(data);
    }

    public void setVector3i(EntityData data, Vector3i value) {
        if (value == null) value = Vector3i.ZERO;
        Vector3i oldValue = this.data.getPos(data);
        if (!Objects.equals(oldValue, value)) {
            this.data.putPos(data, value);
            this.dataChangeSet.putPos(data, value);
        }
    }

    public Vector3f getVector3f(EntityData data) {
        return this.data.getVector3f(data);
    }

    public void setVector3f(EntityData data, Vector3f value) {
        if (value == null) value = Vector3f.ZERO;
        Vector3f oldValue = this.data.getVector3f(data);
        if (!Objects.equals(oldValue, value)) {
            this.data.putVector3f(data, value);
            this.dataChangeSet.putVector3f(data, value);
        }
    }

    public Object get(EntityData data) {
        return this.data.ensureAndGet(data);
    }

    public boolean getFlag(EntityFlag flag) {
        return flags.getFlag(flag);
    }

    public void setFlag(EntityFlag flag, boolean value) {
        boolean oldValue = this.flags.getFlag(flag);
        if (value != oldValue) {
            this.flags.setFlag(flag, value);
            this.dataChangeSet.putFlags(this.flags);
        }
    }
}
