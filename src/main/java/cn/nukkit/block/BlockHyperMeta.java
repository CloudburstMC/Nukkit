package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.MutableBlockState;

@PowerNukkitOnly
@Since("1.3.0.0-PN")
@Deprecated
@DeprecationDetails(reason = "Deprecated in favor of the new block properties system", since = "1.4.0.0-PN")
public interface BlockHyperMeta {
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    int HYPER_DATA_BITS = 32;

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    long HYPER_DATA_MASK = 0xFFFFFFFF;
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    int getId();

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    int getDamage();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    MutableBlockState getBlockState();
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Deprecated in favor of the new block properties system", since = "1.4.0.0-PN")
    default long getHyperId() {
        return getBlockState().getHyperId();
    }
}
