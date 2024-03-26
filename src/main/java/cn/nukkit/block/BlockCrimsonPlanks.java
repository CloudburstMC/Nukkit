package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockCrimsonPlanks extends BlockSolid {

    public BlockCrimsonPlanks() {
        this(0);
    }

    public BlockCrimsonPlanks(int meta) {
        // super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Planks";
    }

    @Override
    public int getId() {
        return CRIMSON_PLANKS;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_STEM_BLOCK_COLOR;
    }
}
