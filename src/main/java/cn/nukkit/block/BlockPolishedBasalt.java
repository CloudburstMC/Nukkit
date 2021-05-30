package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockPolishedBasalt extends BlockBasalt {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockPolishedBasalt() { this(0); }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockPolishedBasalt(int meta) { super(meta); }

    @Override
    public String getName() {
        return "Polished Basalt";
    }

    @Override
    public int getId() {
        return BlockID.POLISHED_BASALT;
    }
}
