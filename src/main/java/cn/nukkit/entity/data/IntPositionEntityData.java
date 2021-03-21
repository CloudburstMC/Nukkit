package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class IntPositionEntityData extends EntityData<BlockVector3> {
    public int x;
    public int y;
    public int z;

    public IntPositionEntityData(int id, int x, int y, int z) {
        super(id);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public IntPositionEntityData(int id, Vector3 pos) {
        this(id, (int) pos.x, (int) pos.y, (int) pos.z);
    }

    @Override
    public BlockVector3 getData() {
        return new BlockVector3(x, y, z);
    }

    @Override
    public void setData(BlockVector3 data) {
        if (data != null) {
            this.x = data.x;
            this.y = data.y;
            this.z = data.z;
        }
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_POS;
    }
}
