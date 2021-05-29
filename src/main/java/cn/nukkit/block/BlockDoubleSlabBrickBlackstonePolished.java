package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockDoubleSlabBrickBlackstonePolished extends BlockDoubleSlabBlackstonePolished {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockDoubleSlabBrickBlackstonePolished() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockDoubleSlabBrickBlackstonePolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB;
    }

    @Override
    public int getSingleSlabId() {
        return POLISHED_BLACKSTONE_BRICK_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Polished Blackstone Brick";
    }

    @Override
    public double getHardness() {
        return 2;
    }
}
