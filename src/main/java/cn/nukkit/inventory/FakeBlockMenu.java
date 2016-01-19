package cn.nukkit.inventory;

import cn.nukkit.level.Location;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FakeBlockMenu extends Location implements InventoryHolder {

    private Inventory inventory;

    public FakeBlockMenu(Inventory inventory, Location pos) {
        super(pos.x, pos.y, pos.z);
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
