package cn.nukkit.entity.data.entries;

import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PositionEntityDataEntry implements EntityDataEntry {
    public int x;
    public int y;
    public int z;

    public PositionEntityDataEntry() {

    }

    public PositionEntityDataEntry(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PositionEntityDataEntry(Vector3 pos) {
        this.x = (int) pos.x;
        this.y = (int) pos.y;
        this.z = (int) pos.z;
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_POS;
    }
}
