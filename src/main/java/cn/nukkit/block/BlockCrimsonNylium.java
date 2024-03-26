package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCrimsonNylium extends BlockNylium {

    public BlockCrimsonNylium() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Crimson Nylium";
    }

    @Override
    public int getId() {
        return CRIMSON_NYLIUM;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_NYLIUM_BLOCK_COLOR;
    }
}
