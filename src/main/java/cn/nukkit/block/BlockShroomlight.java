package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockShroomlight extends BlockTransparent {
    @Override
    public int getId() {
        return SHROOMLIGHT;
    }

    @Override
    public String getName() {
        return "Shroomlight";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HANDS_ONLY; //TODO Should be hoe, fix at https://github.com/PowerNukkit/PowerNukkit/pull/367
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
