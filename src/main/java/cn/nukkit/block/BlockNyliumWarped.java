package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockNyliumWarped extends BlockNylium {
    
    @Override
    public String getName() {
        return "Warped Nylium";
    }

    @Override
    public int getId() {
        return WARPED_NYLIUM;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_NYLIUM_BLOCK_COLOR;
    }
}
