package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockHoneycombBlock extends BlockSolid {

    @Override
    public String getName() {
        return "Honeycomb Block";
    }

    @Override
    public int getId() {
        return HONEYCOMB_BLOCK;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
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
        return ItemTool.TYPE_NONE;
    }
}
