package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockStairsBlackstone extends BlockStairs {
    public BlockStairsBlackstone() {
        this(0);
    }

    public BlockStairsBlackstone(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Blackstone Stairs";
    }

    @Override
    public int getId() {
        return BLACKSTONE_STAIRS;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
