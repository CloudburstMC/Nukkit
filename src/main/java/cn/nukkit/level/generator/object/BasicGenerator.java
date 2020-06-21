package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

public abstract class BasicGenerator {

    //also autism, see below
    public abstract boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position);

    public void setDecorationDefaults() {
    }

    protected void setBlockAndNotifyAdequately(final ChunkManager level, final BlockVector3 pos, final Block state) {
        this.setBlock(level, new Vector3(pos.x, pos.y, pos.z), state);
    }

    protected void setBlockAndNotifyAdequately(final ChunkManager level, final Vector3 pos, final Block state) {
        this.setBlock(level, pos, state);
    }

    //what autism is this? why are we using floating-point vectors for setting block IDs?
    protected void setBlock(final ChunkManager level, final Vector3 v, final Block b) {
        level.setBlockAt((int) v.x, (int) v.y, (int) v.z, b.getId(), b.getDamage());
    }

}
