package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.DEPRECATED;
import static cn.nukkit.blockproperty.CommonBlockProperties.PILLAR_AXIS;

public abstract class BlockStem extends BlockLog {
    public BlockStem(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
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
