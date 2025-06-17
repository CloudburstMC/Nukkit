package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBambooPlanks extends BlockPlanks {

    public BlockBambooPlanks() {
        this(0);
    }

    public BlockBambooPlanks(int meta) {
         super(0);
    }

    @Override
    public int getId() {
        return BAMBOO_PLANKS;
    }

    @Override
    public String getName() {
        return "Bamboo Planks";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}