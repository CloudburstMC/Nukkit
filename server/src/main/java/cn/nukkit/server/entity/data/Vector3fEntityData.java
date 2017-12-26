package cn.nukkit.server.entity.data;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Vector3fEntityData extends EntityData<Vector3> {
    public float x;
    public float y;
    public float z;

    public Vector3fEntityData(int id, float x, float y, float z) {
        super(id);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3fEntityData(int id, Vector3 pos) {
        this(id, pos.x, pos.y, pos.z);
    }

    @Override
    public Vector3 getData() {
        return new Vector3(x, y, z);
    }

    @Override
    public void setData(Vector3 data) {
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
