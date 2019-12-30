package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockBedrock extends BlockSolid {

    public BlockBedrock(Identifier id) {
        super(id);
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
