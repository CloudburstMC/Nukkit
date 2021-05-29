package cn.nukkit.utils.functional;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@FunctionalInterface
public interface BlockPositionDataConsumer<D> {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    void accept(int x, int y, int z, D data);
}
