package cn.nukkit.level.format.updater;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@FunctionalInterface
interface Updater {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state);
}
