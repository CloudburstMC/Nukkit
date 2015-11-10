package cn.nukkit.entity.data.entries;

import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LongEntityDataEntry implements EntityDataEntry {
    public long data;

    public LongEntityDataEntry() {

    }

    public LongEntityDataEntry(long data) {
        this.data = data;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_LONG;
    }
}
