package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockDoubleSlabBase extends BlockSolidMeta {
    protected BlockDoubleSlabBase(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Double "+getSlabName()+" Slab";
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract String getSlabName();
}
