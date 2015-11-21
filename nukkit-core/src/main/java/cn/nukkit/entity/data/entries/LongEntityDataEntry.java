package cn.nukkit.entity.data.entries;

import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LongEntityDataEntry implements EntityDataEntry<Long> {
    public long data;

    public LongEntityDataEntry() {

    }

    public LongEntityDataEntry(long data) {
        this.data = data;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_LONG;
    }
}
