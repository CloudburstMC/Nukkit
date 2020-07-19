package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.BlockFace;

public interface Faceable {

    BlockFace getBlockFace();
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    default void setBlockFace(BlockFace face) {
        // Does nothing by default
    }
}
