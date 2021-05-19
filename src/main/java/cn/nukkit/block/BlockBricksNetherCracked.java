package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockBricksNetherCracked extends BlockBricksNether {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockBricksNetherCracked() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CRACKED_NETHER_BRICKS;
    }

    @Override
    public String getName() {
        return "Cracked Nether Bricks";
    }
}
