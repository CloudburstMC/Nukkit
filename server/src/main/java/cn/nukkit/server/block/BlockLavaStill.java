package cn.nukkit.server.block;

import cn.nukkit.server.level.NukkitLevel;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockLavaStill extends BlockLava {

    public BlockLavaStill() {
        super(0);
    }

    public BlockLavaStill(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STILL_LAVA;
    }

    @Override
    public String getName() {
        return "Still Lava";
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return new BlockLavaStill(meta);
    }

    @Override
    public int onUpdate(int type) {
        if (type != NukkitLevel.BLOCK_UPDATE_SCHEDULED) {
            return super.onUpdate(type);
        }
        return 0;
    }
}
