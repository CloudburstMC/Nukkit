package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FloatEntityData implements EntityData<Float> {
    public float data;

    public FloatEntityData() {

    }

    public FloatEntityData(float data) {
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
