package cn.nukkit.inventory;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import com.nukkitx.math.vector.Vector3i;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FakeBlockMenu implements InventoryHolder {

    private final Inventory inventory;
    private final Block block;

    public FakeBlockMenu(Inventory inventory, Block block) {
        this.inventory = inventory;
        this.block = block;
    }

    public Vector3i getPosition() {
        return block.getPosition();
    }

    public Level getLevel() {
        return block.getLevel();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
