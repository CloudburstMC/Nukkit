package cn.nukkit.entity.data.entries;

import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FloatEntityDataEntry implements EntityDataEntry<Float> {
    public float data;

    public FloatEntityDataEntry() {

    }

    public FloatEntityDataEntry(float data) {
        this.data = data;
    }

    public Float getData() {
        return data;
    }

    public void setData(Float data) {
        if (data == null) {
            this.data = 0;
        } else {
            this.data = data;
        }

    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_FLOAT;
    }
}
