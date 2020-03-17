package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Furnace;
import cn.nukkit.utils.Identifier;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockFurnace extends BlockFurnaceBurning {

    protected BlockFurnace(Identifier id, BlockEntityType<? extends Furnace> furnace) {
        super(id, furnace);
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