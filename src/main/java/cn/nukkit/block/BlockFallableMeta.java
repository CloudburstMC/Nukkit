package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;

import javax.annotation.Nonnull;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockFallableMeta extends BlockFallable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockFallableMeta() {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockFallableMeta(int meta) {
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
