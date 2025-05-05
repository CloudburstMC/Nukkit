package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockOreEmeraldDeepslate extends BlockOreEmerald {

    public BlockOreEmeraldDeepslate() {
    }

    @Override
    public int getId() {
        return DEEPSLATE_EMERALD_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Deepslate Emerald Ore";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY_BLOCK_COLOR;
    }
}
