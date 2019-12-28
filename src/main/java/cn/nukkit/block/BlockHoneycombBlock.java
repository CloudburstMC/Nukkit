package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockHoneycombBlock extends BlockSolid {

    public BlockHoneycombBlock() {

    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HANDS_ONLY;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public int getId() {
        return HONEYCOMB_BLOCK;
    }

    @Override
    public String getName() {
        return "Honeycomb Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
