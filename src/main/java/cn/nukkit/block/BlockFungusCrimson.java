package cn.nukkit.block;

import cn.nukkit.Player;

import javax.annotation.Nullable;

public class BlockFungusCrimson extends BlockFungus {
    
    @Override
    public int getId() {
        return CRIMSON_FUNGUS;
    }

    @Override
    public String getName() {
        return "Crimson Fungus";
    }

    @Override
    protected boolean canGrowOn(Block support) {
        return support.getId() == CRIMSON_NYLIUM;
    }

    @Override
    public boolean grow(@Nullable Player cause) {
        // TODO Make it grow
        return false;
    }
}
