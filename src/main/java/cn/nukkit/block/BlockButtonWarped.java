package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockButtonWarped extends BlockButtonWooden {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockButtonWarped() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockButtonWarped(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return WARPED_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Warped Button";
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
