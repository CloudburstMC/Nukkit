package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;

@PowerNukkitOnly
@Since("1.3.0.0-PN")
@Deprecated
@DeprecationDetails(reason = "Deprecated in favor of the new block properties system", since = "1.4.0.0-PN")
public abstract class BlockTransparentHyperMeta extends BlockTransparentMeta implements BlockHyperMeta {
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public BlockTransparentHyperMeta() {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public BlockTransparentHyperMeta(int meta) {
        super(meta);
    }

    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.LEGACY_HYPER_PROPERTIES;
    }

    @Override
    public abstract Item toItem();
}
