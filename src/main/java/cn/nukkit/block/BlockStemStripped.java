package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;

import javax.annotation.Nonnull;

public abstract class BlockStemStripped extends BlockStem {
    public BlockStemStripped(int meta) {
        super(meta);
    }

    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PILLAR_DEPRECATED_PROPERTIES;
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
    public boolean onActivate(Item item, Player player) {
        return false;
    }
}
