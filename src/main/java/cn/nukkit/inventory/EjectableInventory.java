package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class EjectableInventory extends ContainerInventory {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public EjectableInventory(InventoryHolder holder, InventoryType type) {
        super(holder, type);
    }
}
