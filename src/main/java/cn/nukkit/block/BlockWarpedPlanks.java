package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockWarpedPlanks extends BlockSolid {

    public BlockWarpedPlanks() {
        this(0);
    }

    public BlockWarpedPlanks(int meta) {
        // super(meta);
    }

    @Override
    public int getId() {
        return WARPED_PLANKS;
    }

    @Override
    public String getName() {
        return "Warped Planks";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }
}