package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockBricksBlackstonePolishedCracked extends BlockBricksBlackstonePolished {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockBricksBlackstonePolishedCracked() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CRACKED_POLISHED_BLACKSTONE_BRICKS;
    }

    @Override
    public String getName() {
        return "Cracked Polished Blackstone Bricks";
    }
}
