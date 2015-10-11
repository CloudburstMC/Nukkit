package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SimpleTransactionGroup implements TransactionGroup {

    private long creationTime;
    protected boolean hasExecuted = true;

    protected Player source = null;

    protected Set<Inventory> inventories = new HashSet<>();

    protected Set<Transaction> transactions = new HashSet<>();

    public SimpleTransactionGroup() {
        this(null);
    }

    public SimpleTransactionGroup(Player source) {
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
    public Set<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        if (this.transactions.contains(transaction)) {
            return;
        }

        for (Transaction tx : this.transactions) {
            if (tx.getInventory().equals(transaction.getInventory()) && tx.getSlot() == transaction.getSlot()) {
                if (transaction.getCreationTime() >= tx.getCreationTime()) {
                    this.transactions.remove(tx);
                } else {
                    return;
                }
            }
        }

        this.transactions.add(transaction);
        this.inventories.add(transaction.getInventory());
    }

    protected boolean matchItems(List<Item> needItems, List<Item> haveItems) {
        for (Transaction ts : this.transactions) {
            if (ts.getTargetItem().getId() != Item.AIR) {
                needItems.add(ts.getTargetItem());
            }
            Item checkSourceItem = ts.getInventory().getItem(ts.getSlot());
            Item sourceItem = ts.getSourceItem();
            if (!checkSourceItem.equals(sourceItem, true) || sourceItem.getCount() != checkSourceItem.getCount()) {
                return false;
            }
            if (sourceItem.getId() != Item.AIR) {
                haveItems.add(sourceItem);
            }
        }

        for (Item needItem : needItems) {
            for (Item haveItem : haveItems) {
                if (needItem.equals(haveItem, true)) {
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

        return this.matchItems(needItems, haveItems) && haveItems.isEmpty() && needItems.isEmpty() && !this.transactions.isEmpty();
    }

    @Override
    public boolean execute() {
        if (this.hasExecuted || !this.canExecute()) {
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

        for (Transaction transaction : this.transactions) {
            transaction.getInventory().setItem(transaction.getSlot(), transaction.getTargetItem());
        }

        this.hasExecuted = true;

        return true;
    }

    @Override
    public boolean hasExecuted() {
        return this.hasExecuted;
    }
}
