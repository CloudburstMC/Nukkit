package cn.nukkit.entity.data.entries;

import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShortEntityDataEntry implements EntityDataEntry {
    public int data;

    public ShortEntityDataEntry() {

    }

    public ShortEntityDataEntry(int data) {
        this.data = data;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_SHORT;
    }
}
