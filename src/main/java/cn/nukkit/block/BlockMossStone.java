package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockMossStone extends BlockSolid {

    public BlockMossStone() {
    }

    @Override
    public String getName() {
        return "Moss Stone";
    }

    @Override
    public int getId() {
        return MOSS_STONE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
