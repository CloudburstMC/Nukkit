package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockPressurePlateWarped extends BlockPressurePlateWood {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockPressurePlateWarped() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockPressurePlateWarped(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return WARPED_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Warped Pressure Plate";
    }
    
    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }
}
