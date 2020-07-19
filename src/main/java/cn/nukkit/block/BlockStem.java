package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.DEPRECATED;
import static cn.nukkit.blockproperty.CommonBlockProperties.PILLAR_AXIS;

public abstract class BlockStem extends BlockLog {
    protected static final BlockProperties PILLAR_DEPRECATED_PROPERTIES = new BlockProperties(PILLAR_AXIS, DEPRECATED);
    
    public BlockStem(int meta) {
        super(meta);
    }

    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PILLAR_PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 2;
    }
}
