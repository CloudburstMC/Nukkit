package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.List;

public abstract class BasicGenerator {

    public abstract boolean generate(ChunkManager level, List<Block> blocks, NukkitRandom rand, BlockVector3 position);

    @Deprecated
    public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position) {
        return false;
    }

    public void setDecorationDefaults() {
    }

    protected void setBlockAndNotifyAdequately(List<Block> blocks, BlockVector3 pos, Block state) {
        setBlock(blocks, pos, state);
    }

    protected void setBlock(List<Block> blocks, BlockVector3 v, Block b) {
        blocks.add(Block.get(b.getId(), b.getDamage(), new Position(v.x, v.y, v.z)));
    }

    @Deprecated
    protected void setBlockAndNotifyAdequately(ChunkManager level, BlockVector3 pos, Block state) {
        setBlock(level, new Vector3(pos.x, pos.y, pos.z), state);
    }

    @Deprecated
    protected void setBlockAndNotifyAdequately(ChunkManager level, Vector3 pos, Block state) {
        setBlock(level, pos, state);
    }

    @Deprecated
    protected void setBlock(ChunkManager level, Vector3 v, Block b) {
        level.setBlockAt((int) v.x, (int) v.y, (int) v.z, b.getId(), b.getDamage());
    }

}
