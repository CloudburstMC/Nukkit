package cn.nukkit.server.entity.data;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.math.Vector3f;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Vector3fEntityData extends EntityData<Vector3f> {
    public float x;
    public float y;
    public float z;

    public Vector3fEntityData(int id, float x, float y, float z) {
        super(id);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3fEntityData(int id, Vector3f pos) {
        this(id, pos.x, pos.y, pos.z);
    }

    @Override
    public Vector3f getData() {
        return new Vector3f(x, y, z);
    }

    @Override
    public void setData(Vector3f data) {
        if (data != null) {
            this.x = data.x;
            this.y = data.y;
            this.z = data.z;
        }
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_VECTOR3F;
    }
}
