package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockStairsBrickBlackstonePolished extends BlockStairsBlackstonePolished {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStairsBrickBlackstonePolished() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStairsBrickBlackstonePolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_BRICK_STAIRS;
    }

    @Override
    public String getName() {
        return "Polished Blackstone Brick Stairs";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }
}
