package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.3.0.0-PN")
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
    @Since("1.3.0.0-PN")
    default long getHyperId() {
        return (((long) getId()) << HYPER_DATA_BITS) | getDamage();
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Does not hold all hyper-id metadata", since = "1.3.0.0-PN")
    default int getFullId() {
        return (getId() << Block.DATA_BITS) | (getDamage() & Block.DATA_MASK);
    }
}
