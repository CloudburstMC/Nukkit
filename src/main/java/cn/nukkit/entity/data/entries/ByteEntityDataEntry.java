package cn.nukkit.entity.data.entries;

import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ByteEntityDataEntry implements EntityDataEntry {
    public byte data;

    public ByteEntityDataEntry() {

    }

    public ByteEntityDataEntry(byte data) {
        this.data = data;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_BYTE;
    }
}
