package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

@PowerNukkitOnly
@Since("1.3.0.0-PN")
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
    public int getFullId() {
        return BlockHyperMeta.super.getFullId();
    }

    @Override
    public abstract Item toItem();
}
