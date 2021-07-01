package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class BlockFenceGateCrimson extends BlockFenceGate {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockFenceGateCrimson() {
        this(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockFenceGateCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Crimson Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
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
