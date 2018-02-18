package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Old113InventoryTransaction extends InventoryTransaction {

    private final long creationTime;
    protected boolean hasExecuted = false;

    protected Player source = null;

    protected final Set<Inventory> inventories = new HashSet<>();

    protected final Set<InventoryAction> actions = new HashSet<>();

    public Old113InventoryTransaction() {
        this(null);
    }

    public Old113InventoryTransaction(Player source) {
        super (source, new ArrayList<>());
        this.creationTime = System.currentTimeMillis();
        this.source = source;
    }

    public Player getSource() {
        return source;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public Set<Inventory> getInventories() {
        return inventories;
    }

    @Override
    public Set<InventoryAction> getActions() {
        return actions;
    }

    @Override
    public void addAction(InventoryAction aaction) {
        if (this.actions.contains(aaction)) {
            return;
        }
        if (!(aaction instanceof SlotChangeAction)) return;
        SlotChangeAction action = (SlotChangeAction) aaction;

        for (InventoryAction txx : new HashSet<>(this.actions)) {
            if (!(txx instanceof SlotChangeAction)) continue;
            SlotChangeAction tx = (SlotChangeAction) txx;
            if (tx.getInventory().equals(action.getInventory()) && tx.getSlot() == action.getSlot()) {
                if (action.getCreationTime() >= tx.getCreationTime()) {
                    this.actions.remove(tx);
                } else {
                    return;
                }
            }
        }

        this.actions.add(action);
        this.inventories.add(action.getInventory());
    }

    protected boolean matchItems(List<Item> needItems, List<Item> haveItems) {
        for (InventoryAction tss : this.actions) {
            if (!(tss instanceof SlotChangeAction)) continue;
            SlotChangeAction ts = (SlotChangeAction) tss;
            if (ts.getTargetItem().getId() != Item.AIR) {
                needItems.add(ts.getTargetItem());
            }
            Item checkSourceItem = ts.getInventory().getItem(ts.getSlot());
            Item sourceItem = ts.getSourceItem();
            if (!checkSourceItem.equalsExact(sourceItem) && sourceItem.getCount() != checkSourceItem.getCount()) {
                return false;
            }
            if (sourceItem.getId() != Item.AIR) {
                haveItems.add(sourceItem);
            }
        }

        for (Item needItem : new ArrayList<>(needItems)) {
            for (Item haveItem : new ArrayList<>(haveItems)) {
                if (needItem.equals(haveItem, true, true)) {
                    int amount = Math.min(haveItem.getCount(), needItem.getCount());
                    needItem.setCount(needItem.getCount() - amount);
                    haveItem.setCount(haveItem.getCount() - amount);
                    if (haveItem.getCount() == 0) {
                        haveItems.remove(haveItem);
                    }
                    if (needItem.getCount() == 0) {
                        needItems.remove(needItem);
                        break;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean canExecute() {
        List<Item> haveItems = new ArrayList<>();
        List<Item> needItems = new ArrayList<>();

        return this.matchItems(needItems, haveItems) && haveItems.isEmpty() &&
                needItems.isEmpty() && !this.actions.isEmpty();
    }

    @Override
    public boolean execute() {
        return execute(false);
    }

    public boolean execute(boolean force) {
        if (this.hasExecuted || (!force && !this.canExecute())) {
            return false;
        }

        InventoryTransactionEvent ev = new InventoryTransactionEvent(this);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            for (Inventory inventory : this.inventories) {
                if (inventory instanceof PlayerInventory) {
                    ((PlayerInventory) inventory).sendArmorContents(this.getSource());
                }
                inventory.sendContents(this.getSource());
            }
            return false;
        }

        for (InventoryAction transaction : this.actions) {
            if (!(transaction instanceof SlotChangeAction)) continue;
            SlotChangeAction action = (SlotChangeAction) transaction;
            action.getInventory().setItem(action.getSlot(), action.getTargetItem());
        }

        this.hasExecuted = true;

        return true;
    }

    @Override
    public boolean hasExecuted() {
        return this.hasExecuted;
    }

}