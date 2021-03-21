package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
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

    /**
     * A backward-compatibility properties definition that accepts any values that can be stored in the amount of bits
     * specified by {@link Block#DATA_BITS}.
     * <p>Implementations can change the returned properties to restrict only valid values and make complete use
     * of the block state system.</p>
     */
    @Since("1.4.0.0-PN")
    @Nonnull
    @PowerNukkitOnly
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.LEGACY_PROPERTIES;
    }
}
