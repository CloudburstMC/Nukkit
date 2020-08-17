package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockSlabBrickBlackstonePolished extends BlockSlabBlackstonePolished {
    public BlockSlabBrickBlackstonePolished() {
        this(0);
    }

    public BlockSlabBrickBlackstonePolished(int meta) {
        super(meta, POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB);
    }

    protected BlockSlabBrickBlackstonePolished(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
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
