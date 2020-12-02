package cn.nukkit.dispenser;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;

@PowerNukkitDifference(info = "Spend items in container, the dropper faces to (if there is one).", since = "1.4.0.0-PN")
@PowerNukkitOnly
public class DropperDispenseBehavior extends DefaultDispenseBehavior {
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (block.level.getBlockEntityIfLoaded(target) instanceof InventoryHolder) {
            InventoryHolder invHolder = (InventoryHolder) block.level.getBlockEntityIfLoaded(target);
            Inventory inv = invHolder.getInventory();
            Item clone = item.clone();
            clone.count = 1;

            if (inv.canAddItem(clone)) {
                inv.addItem(clone);
            } else {
                return clone;
            }
        } else {
            block.level.addSound(block, Sound.RANDOM_CLICK);
            return super.dispense(block, face, item);
        }
        return null;
    }
}
