package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockOreCoalDeepslate extends BlockOreCoal {

    public BlockOreCoalDeepslate() {
    }

    @Override
    public int getId() {
        return DEEPSLATE_COAL_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Deepslate Coal Ore";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY_BLOCK_COLOR;
    }
}
