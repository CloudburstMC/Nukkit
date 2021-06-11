package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.blockproperty.value.WoodType;

@PowerNukkitOnly
public class BlockWoodStrippedDarkOak extends BlockWoodStripped {
    @PowerNukkitOnly
    public BlockWoodStrippedDarkOak() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockWoodStrippedDarkOak(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_DARK_OAK_LOG;
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.DARK_OAK;
    }
}
