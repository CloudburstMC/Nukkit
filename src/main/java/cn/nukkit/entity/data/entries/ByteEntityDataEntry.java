package cn.nukkit.entity.data.entries;

import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ByteEntityDataEntry implements EntityDataEntry<Byte> {
    public byte data;

    public ByteEntityDataEntry() {

    }

    public ByteEntityDataEntry(byte data) {
        this.data = data;
    }

    public Byte getData() {
        return data;
    }

    public void setData(Byte data) {
        if (data == null) {
            this.data = 0;
        } else {
            this.data = data;
        }
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_BYTE;
    }
}
