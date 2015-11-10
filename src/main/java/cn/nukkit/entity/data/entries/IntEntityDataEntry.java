package cn.nukkit.entity.data.entries;

import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IntEntityDataEntry implements EntityDataEntry {
    public int data;

    public IntEntityDataEntry() {

    }

    public IntEntityDataEntry(int data) {
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
        return Entity.DATA_TYPE_INT;
    }
}
