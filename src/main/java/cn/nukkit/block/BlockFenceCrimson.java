package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockFenceCrimson extends BlockFenceBase {

    public BlockFenceCrimson() {
        this(0);
    }

    public BlockFenceCrimson(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Fence";
    }

    @Override
    public int getId() {
        return CRIMSON_FENCE;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }
}
