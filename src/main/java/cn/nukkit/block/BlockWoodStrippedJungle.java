package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.blockproperty.value.WoodType;

@PowerNukkitOnly
public class BlockWoodStrippedJungle extends BlockWoodStripped {
    @PowerNukkitOnly
    public BlockWoodStrippedJungle() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockWoodStrippedJungle(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_JUNGLE_LOG;
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.JUNGLE;
    }
}
