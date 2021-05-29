package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockButtonCrimson extends BlockButtonWooden {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockButtonCrimson() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockButtonCrimson(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return CRIMSON_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Crimson Button";
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
