package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockMud extends BlockSolid {

    public BlockMud() {
    }

    @Override
    public String getName() {
        return "Mud";
    }

    @Override
    public int getId() {
        return MUD;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_TERRACOTA_BLOCK_COLOR;
    }
}
