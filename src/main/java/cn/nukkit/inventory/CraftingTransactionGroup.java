package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftingTransactionGroup extends SimpleTransactionGroup {

    protected List<Item> input = new ArrayList<>();

    protected List<Item> output = new ArrayList<>();

    protected Recipe recipe = null;

    public CraftingTransactionGroup(SimpleTransactionGroup group) {
        super();
        this.transactions = group.getTransactions();
        this.inventories = group.getInventories();
        this.source = group.getSource();

        this.matchItems(this.output, this.input);
    }

    @Override
    public void addTransaction(Transaction transaction) {
        super.addTransaction(transaction);
        this.input = new ArrayList<>();
        this.output = new ArrayList<>();
        this.matchItems(this.output, this.input);
    }

    public List<Item> getRecipe() {
        return this.input;
    }

    public Item getResult() {
        return this.output.get(0);
    }

    @Override
    public boolean canExecute() {
        if (this.output.size() != 1 || this.input.size() == 0) {
            return false;
        }

        return this.getMatchingRecipe() != null;
    }

    public Recipe getMatchingRecipe() {
        if (this.recipe == null) {
            this.recipe = Server.getInstance().getCraftingManager().matchTransaction(this);
        }

        return this.recipe;
    }

    @Override
    public boolean execute() {
        if (this.hasExecuted || !this.canExecute()) {
            return false;
        }

        CraftItemEvent ev = new CraftItemEvent(this, this.getMatchingRecipe());
        Server.getInstance().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            for (Inventory inventory : this.inventories) {
                inventory.sendContents(inventory.getViewers());
            }

            return false;
        }

        for (Transaction transaction : this.transactions) {
            transaction.getInventory().setItem(transaction.getSlot(), transaction.getTargetItem());
        }
        this.hasExecuted = true;

        return true;
    }
}
