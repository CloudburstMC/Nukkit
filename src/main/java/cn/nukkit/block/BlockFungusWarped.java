package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

public class BlockFungusWarped extends BlockFungus {
    
    @Override
    public int getId() {
        return WARPED_FUNGUS;
    }

    @Override
    public String getName() {
        return "Warped Fungus";
    }

    @Override
    protected boolean canGrowOn(Block support) {
        return support.getId() == WARPED_NYLIUM;
    }

    @Override
    public boolean grow(@Nullable Player cause) {
        // TODO Make it grow
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }
}
