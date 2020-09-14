package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockStairsSmoothRedSandstone extends BlockStairs {
    public BlockStairsSmoothRedSandstone() {
        this(0);
    }

    public BlockStairsSmoothRedSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SMOOTH_RED_SANDSTONE_STAIRS;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
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
    public String getName() {
        return "Smooth Red Sandstone Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
