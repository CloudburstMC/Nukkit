package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.blockproperty.value.WoodType;

@PowerNukkitOnly
public class BlockWoodStrippedSpruce extends BlockWoodStripped {
    @PowerNukkitOnly
    public BlockWoodStrippedSpruce() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockWoodStrippedSpruce(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_SPRUCE_LOG;
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.SPRUCE;
    }
}
