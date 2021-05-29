package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockSoulLantern extends BlockLantern {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockSoulLantern() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockSoulLantern(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SOUL_LANTERN;
    }

    @Override
    public String getName() {
        return "Soul Lantern";
    }

    @Override
    public int getLightLevel() {
        return 10;
    }

}
