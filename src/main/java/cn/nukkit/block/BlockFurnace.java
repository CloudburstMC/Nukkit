package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockFurnace extends BlockFurnaceBurning {

    public BlockFurnace(Identifier id) {
        super(id);
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}