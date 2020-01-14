package cn.nukkit.inventory;

import cn.nukkit.level.BlockPosition;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FakeBlockMenu extends BlockPosition implements InventoryHolder {

    private final Inventory inventory;

    public FakeBlockMenu(Inventory inventory, BlockPosition pos) {
        super(pos.x, pos.y, pos.z, pos.level);
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
