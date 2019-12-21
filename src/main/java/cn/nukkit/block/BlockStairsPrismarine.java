package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockStairsPrismarine extends BlockStairs {
    public BlockStairsPrismarine() {
        this(0);
    }

    public BlockStairsPrismarine(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PRISMARINE_STAIRS;
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
    public String getName() {
        return "Prismarine Stairs";
    }

    //TODO: Prismarine block color?
    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
