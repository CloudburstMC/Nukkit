package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.value.SmallFlowerType;

import javax.annotation.Nonnull;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockDandelion extends BlockFlower {
    public BlockDandelion() {
        this(0);
    }

    public BlockDandelion(int meta) {
        super(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @Override
    public int getId() {
        return DANDELION;
    }

    @Override
    protected Block getUncommonFlower() {
        return get(POPPY);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setFlowerType(SmallFlowerType flowerType) {
        setOnSingleFlowerType(SmallFlowerType.DANDELION, flowerType);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public SmallFlowerType getFlowerType() {
        return SmallFlowerType.DANDELION;
    }
}
