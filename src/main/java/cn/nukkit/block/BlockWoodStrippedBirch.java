package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.blockproperty.value.WoodType;

@PowerNukkitOnly
public class BlockWoodStrippedBirch extends BlockWoodStripped {
    @PowerNukkitOnly
    public BlockWoodStrippedBirch() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockWoodStrippedBirch(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_BIRCH_LOG;
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.BIRCH;
    }
}
