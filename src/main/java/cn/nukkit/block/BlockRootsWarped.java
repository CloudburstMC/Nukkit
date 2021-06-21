package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockRootsWarped extends BlockRoots {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockRootsWarped() {
        // Does nothing
    }

    @Override
    public int getId() {
        return WARPED_ROOTS;
    }

    @Override
    public String getName() {
        return "Warped Roots";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }
}
