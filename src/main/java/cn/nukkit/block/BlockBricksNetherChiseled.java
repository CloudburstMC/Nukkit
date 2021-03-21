package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockBricksNetherChiseled extends BlockBricksNether {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockBricksNetherChiseled() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CHISELED_NETHER_BRICKS;
    }

    @Override
    public String getName() {
        return "Chiseled Nether Bricks";
    }
}
