package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

/**
 * BlockFenceWarped.java was made by using BlockFence.java and BlockFenceNetherBrick.java
 */
/**
 * @author xtypr
 * @since 2015/12/7
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockFenceWarped extends BlockFenceBase {

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockFenceWarped() {
        this(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockFenceWarped(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Warped Fence";
    }

    @Override
    public int getId() {
        return WARPED_FENCE;
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
        return BlockColor.CYAN_BLOCK_COLOR;
    }
}
