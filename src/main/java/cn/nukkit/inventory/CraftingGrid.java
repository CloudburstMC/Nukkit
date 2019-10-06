package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.player.Player;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class CraftingGrid extends BaseInventory {

    public CraftingGrid(InventoryHolder holder) {
        this(holder, 4);
    }

    public CraftingGrid(InventoryHolder holder, int overrideSize) {
        super(holder, InventoryType.CRAFTING, new HashMap<>(), overrideSize);
    }

    @Override
    public int getSize() {
        return 4;
    }

    public void setSize(int size) {
        throw new RuntimeException("Cannot change the size of a crafting grid");
    }

    public String getName() {
        return "Crafting";
    }

    public void removeFromAll(Item item) {
        int count = item.getCount();

        for (int i = 0; i < this.size; i++) {
            Item target = this.getItem(i);

            if (target.equals(item, true, false)) {
                count--;
                target.count--;
                this.setItem(i, target);
                if (count <= 0) break;
            }
        }

        if (count != 0 && log.isDebugEnabled()) {
            log.debug("Unexpected ingredient count (" + count + ") in crafting grid");
        }
    }

    public void sendSlot(int index, Player... target) {
        //we can't send a inventorySlot of a client-sided inventory window
    }

    public void sendContents(Player... target) {
        //we can't send the contents of a client-sided inventory window
    }
}
