package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockFlowable extends BlockTransparent {

    protected BlockFlowable(int meta) {
        super(meta);
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }
}
