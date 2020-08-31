package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockOreIron extends BlockSolid {


    public BlockOreIron() {
    }

    @Override
    public int getId() {
        return IRON_ORE;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public String getName() {
        return "Iron Ore";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
