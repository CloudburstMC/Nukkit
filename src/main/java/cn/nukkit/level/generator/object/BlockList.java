package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;

import java.util.AbstractList;

public class BlockList extends AbstractList<Block> {

    private ChunkManager chunkManager;

    public BlockList(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean add(Block block) {
        this.chunkManager.setBlockAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), block.getId(), block.getDamage());
        return true;
    }

    @Override
    public Block get(int index) {
        return null;
    }

}
