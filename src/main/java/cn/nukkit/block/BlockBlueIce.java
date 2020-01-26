package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

public class BlockBlueIce extends BlockIcePacked {

    public BlockBlueIce(Identifier id) {
        super(id);
    }

    @Override
    public boolean isTransparent() {
        return false;
    }

    @Override
    public double getHardness() {
        return 2.8;
    }

    @Override
    public double getFrictionFactor() {
        return 0.989;
    }
}
