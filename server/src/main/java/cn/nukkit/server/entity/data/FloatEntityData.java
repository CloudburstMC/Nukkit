package cn.nukkit.server.entity.data;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FloatEntityData extends EntityData<Float> {
    public float data;

    public FloatEntityData(int id, float data) {
        super(id);
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
