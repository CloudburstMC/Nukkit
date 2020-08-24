package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * @author xtypr
 * @since 2015/12/7
 */
public class BlockBricksNether extends BlockSolid {

    public BlockBricksNether() {
    }

    @Override
    public String getName() {
        return "Nether Brick";
    }

    @Override
    public int getId() {
        return NETHER_BRICKS;
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
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will return false as expected")
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
