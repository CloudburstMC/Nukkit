package cn.nukkit.block;

import cn.nukkit.level.Level;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockLavaStill extends BlockLava {

    public BlockLavaStill(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Still Lava";
    }

    @Override
    public Block getBlock(int meta) {
        return Block.get(STILL_LAVA);
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_SCHEDULED) {
            return super.onUpdate(type);
        }
        return 0;
    }
}
