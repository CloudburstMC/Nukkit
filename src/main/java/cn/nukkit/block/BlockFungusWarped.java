package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class BlockFungusWarped extends BlockFungus {

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockFungusWarped() {
        // Does nothing
    }

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
