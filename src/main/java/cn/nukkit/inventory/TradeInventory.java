package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.Map;

/**
 * author: NycuRO
 * NukkitX Project
 */
public class TradeInventory extends ContainerInventory {

    protected FakeBlockMenu holder;

    public TradeInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items, int size, String title) {
        super(holder, type, items, size, title);
    }

    @Override
    public InventoryType getType() {
        return InventoryType.TRADING;
    }

    @Override
    public String getName() {
        return "Trade Inventory";
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public FakeBlockMenu getHolder() {
        return this.holder;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        for (int i = 0; i < 2; ++i) {
            this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), this.getItem(i));
            this.clear(i);
        }
    }
}
