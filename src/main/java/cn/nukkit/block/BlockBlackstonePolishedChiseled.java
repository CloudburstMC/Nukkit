package cn.nukkit.block;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockBlackstonePolishedChiseled extends BlockBlackstonePolished {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockBlackstonePolishedChiseled() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CHISELED_POLISHED_BLACKSTONE;
    }

    @Override
    public String getName() {
        return "Chiseled Polished Blackstone";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }
}
