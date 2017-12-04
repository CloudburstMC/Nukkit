package cn.nukkit.server.level.generator.object;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.level.ChunkManager;
import cn.nukkit.server.math.BlockVector3;
import cn.nukkit.server.math.NukkitRandom;
import cn.nukkit.server.math.Vector3;

public abstract class BasicGenerator {

    public abstract boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position);

    public void setDecorationDefaults() {
    }

    protected void setBlockAndNotifyAdequately(ChunkManager level, BlockVector3 pos, Block state) {
        setBlock(level, new Vector3(pos.x, pos.y, pos.z), state);
    }

    protected void setBlockAndNotifyAdequately(ChunkManager level, Vector3 pos, Block state) {
        setBlock(level, pos, state);
    }

    protected void setBlock(ChunkManager level, Vector3 v, Block b) {
        level.setBlockIdAt((int) v.x, (int) v.y, (int) v.z, b.getId());
        level.setBlockDataAt((int) v.x, (int) v.y, (int) v.z, b.getDamage());
    }
}
