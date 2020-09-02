package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;

import javax.annotation.Nonnull;

public abstract class BlockMeta extends Block {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected BlockMeta(){
        // Does nothing
    }
    
    protected BlockMeta(int meta) {
        getMutableState().setDataStorageFromInt(meta);
    }

    @Since("1.4.0.0-PN")
    @Nonnull
    @PowerNukkitOnly
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.LEGACY_PROPERTIES;
    }
}
