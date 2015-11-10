package cn.nukkit.entity.data.entries;

import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FloatEntityDataEntry implements EntityDataEntry {
    public float data;

    public FloatEntityDataEntry() {

    }

    public FloatEntityDataEntry(float data) {
        this.data = data;
    }

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_FLOAT;
    }
}
