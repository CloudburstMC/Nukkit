package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.blockproperty.value.WoodType;

@PowerNukkitOnly
public class BlockWoodStrippedAcacia extends BlockWoodStripped {

    @PowerNukkitOnly
    public BlockWoodStrippedAcacia() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockWoodStrippedAcacia(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_ACACIA_LOG;
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.ACACIA;
    }
}
