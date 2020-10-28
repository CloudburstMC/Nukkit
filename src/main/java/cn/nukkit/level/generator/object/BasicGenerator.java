package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;

import java.util.List;

public abstract class BasicGenerator {

    public abstract boolean generate(ChunkManager level, List<Block> blocks, NukkitRandom rand, BlockVector3 position);

    public void setDecorationDefaults() {
    }

    protected void setBlockAndNotifyAdequately(List<Block> blocks, BlockVector3 pos, Block state) {
        setBlock(blocks, pos, state);
    }

    protected void setBlock(List<Block> blocks, BlockVector3 v, Block b) {
        blocks.add(Block.get(b.getId(), b.getDamage(), new Position(v.x, v.y, v.z)));
    }
}
