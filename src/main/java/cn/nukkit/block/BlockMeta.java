package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;

import javax.annotation.Nonnull;

public abstract class BlockMeta extends Block {
    /**
     * Creates the block in the default state.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected BlockMeta(){
        // Does nothing
    }

    /**
     * Create the block from a specific state.
     * 
     * If the meta is not acceptable by {@link #getProperties()}, it will be modified to an accepted value.
     * 
     * @param meta The block state meta
     */
    protected BlockMeta(int meta) throws InvalidBlockPropertyMetaException {
        if (meta != 0) {
            getMutableState().setDataStorageFromInt(meta, true);
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public abstract BlockProperties getProperties();
}
