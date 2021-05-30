package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class BlockFenceGateWarped extends BlockFenceGate {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockFenceGateWarped() {
        this(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockFenceGateWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Warped Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
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
