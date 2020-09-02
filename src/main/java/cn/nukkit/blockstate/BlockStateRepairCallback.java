package cn.nukkit.blockstate;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.NonNull;

/**
 * A functional interface that is called when a broken block state is being repaired.
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@FunctionalInterface
public interface BlockStateRepairCallback {
    
    /**
     * Called multiple times if necessary when a broken block state is being repaired.
     * @param repair The repair that will be applied
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    void onRepair(@NonNull BlockStateRepair repair);
}
