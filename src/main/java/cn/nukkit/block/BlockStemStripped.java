package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;

import javax.annotation.Nonnull;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockStemStripped extends BlockStem {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStemStripped(int meta) {
        super(meta);
    }

    @Override
    protected BlockState getStrippedState() {
        return getCurrentState();
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        return false;
    }
}
