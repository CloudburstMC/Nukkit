package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author good777LUCKY
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ItemNuggetIron extends Item {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemNuggetIron() {
        this(0, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemNuggetIron(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemNuggetIron(Integer meta, int count) {
        super(IRON_NUGGET, meta, count, "Iron Nugget");
    }
}
