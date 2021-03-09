package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;

import java.util.*;

/**
 * @author CreeperFace
 */
public class InventoryTransaction {

    private long creationTime;
    protected boolean hasExecuted;

    protected Player source;

    protected Set<Inventory> inventories = new HashSet<>();

    protected List<InventoryAction> actions = new ArrayList<>();

    public InventoryTransaction(Player source, List<InventoryAction> actions) {
        this(source, actions, true);
    }

    public InventoryTransaction(Player source, List<InventoryAction> actions, boolean init) {
        if (init) {
            init(source, actions);
        }
    }

    protected void init(Player source, List<InventoryAction> actions) {
        creationTime = System.currentTimeMillis();
        this.source = source;

        for (InventoryAction action : actions) {
            this.addAction(action);
        }
    }

    public Player getSource() {
        return source;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public Set<Inventory> getInventories() {
        return inventories;
    }

    public List<InventoryAction> getActionList() {
        return actions;
    }

    public Set<InventoryAction> getActions() {
        return new HashSet<>(actions);
    }

    public void addAction(InventoryAction action) {
        if (action instanceof SlotChangeAction) {
            SlotChangeAction slotChangeAction = (SlotChangeAction) action;

            ListIterator<InventoryAction> iterator = this.actions.listIterator();

            while (iterator.hasNext()) {
                InventoryAction existingAction = iterator.next();
                if (existingAction instanceof SlotChangeAction) {
                    SlotChangeAction existingSlotChangeAction = (SlotChangeAction) existingAction;
                    if (!existingSlotChangeAction.getInventory().equals(slotChangeAction.getInventory()))
                        continue;
                    Item existingSource = existingSlotChangeAction.getSourceItem();
                    Item existingTarget = existingSlotChangeAction.getTargetItem();
                    if (existingSlotChangeAction.getSlot() == slotChangeAction.getSlot()
                            && slotChangeAction.getSourceItem().equals(existingTarget, existingTarget.hasMeta(), existingTarget.hasCompoundTag())) {
                        iterator.set(new SlotChangeAction(existingSlotChangeAction.getInventory(), existingSlotChangeAction.getSlot(), existingSlotChangeAction.getSourceItem(), slotChangeAction.getTargetItem()));
                        action.onAddToTransaction(this);
                        return;
                    } else if (existingSlotChangeAction.getSlot() == slotChangeAction.getSlot()
                            && slotChangeAction.getSourceItem().equals(existingSource, existingSource.hasMeta(), existingSource.hasCompoundTag())
                            && slotChangeAction.getTargetItem().equals(existingTarget, existingTarget.hasMeta(), existingTarget.hasCompoundTag())) {
                        existingSource.setCount(existingSource.getCount() + slotChangeAction.getSourceItem().getCount());
                        existingTarget.setCount(existingTarget.getCount() + slotChangeAction.getTargetItem().getCount());
                        iterator.set(new SlotChangeAction(existingSlotChangeAction.getInventory(), existingSlotChangeAction.getSlot(), existingSource, existingTarget));
                        return;
                    }
                }
            }
        }
        this.actions.add(action);
        action.onAddToTransaction(this);
    }

    /**
     * This method should not be used by plugins, it's used to add tracked inventories for InventoryActions
     * involving inventories.
     *
     * @param inventory to add
     */
    public void addInventory(Inventory inventory) {
        this.inventories.add(inventory);
    }

    protected boolean matchItems(List<Item> needItems, List<Item> haveItems) {
        for (InventoryAction action : this.actions) {
            if (action.getTargetItem().getId() != Item.AIR) {
                needItems.add(action.getTargetItem());
            }

            if (!action.isValid(this.source)) {
                return false;
            }

            if (action.getSourceItem().getId() != Item.AIR) {
                haveItems.add(action.getSourceItem());
            }
        }

        for (Item needItem : new ArrayList<>(needItems)) {
            for (Item haveItem : new ArrayList<>(haveItems)) {
                if (needItem.equals(haveItem)) {
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

        return haveItems.isEmpty() && needItems.isEmpty();
    }

    protected void sendInventories() {
        for (InventoryAction action : this.actions) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction sca = (SlotChangeAction) action;

                sca.getInventory().sendSlot(sca.getSlot(), this.source);
            }
        }
    }

    public boolean canExecute() {
        List<Item> haveItems = new ArrayList<>();
        List<Item> needItems = new ArrayList<>();
        return matchItems(needItems, haveItems) && this.actions.size() > 0 && haveItems.size() == 0 && needItems.size() == 0;
    }

    protected boolean callExecuteEvent() {
        InventoryTransactionEvent ev = new InventoryTransactionEvent(this);
        this.source.getServer().getPluginManager().callEvent(ev);

        SlotChangeAction from = null;
        SlotChangeAction to = null;
        Player who = null;

        for (InventoryAction action : this.actions) {
            if (!(action instanceof SlotChangeAction)) {
                continue;
            }
            SlotChangeAction slotChange = (SlotChangeAction) action;

            if (slotChange.getInventory().getHolder() instanceof Player) {
                who = (Player) slotChange.getInventory().getHolder();
            }

            if (from == null) {
                from = slotChange;
            } else {
                to = slotChange;
            }
        }

        if (who != null && to != null) {
            if (from.getTargetItem().getCount() > from.getSourceItem().getCount()) {
                from = to;
            }

            InventoryClickEvent ev2 = new InventoryClickEvent(who, from.getInventory(), from.getSlot(), from.getSourceItem(), from.getTargetItem());
            this.source.getServer().getPluginManager().callEvent(ev2);

            if (ev2.isCancelled()) {
                return false;
            }
        }

        return !ev.isCancelled();
    }

    public boolean execute() {
        if (this.hasExecuted() || !this.canExecute()) {
            this.sendInventories();
            return false;
        }


        if (!callExecuteEvent()) {
            this.sendInventories();
            return true;
        }

        for (InventoryAction action : this.actions) {
            if (!action.onPreExecute(this.source)) {
                this.sendInventories();
                return true;
            }
        }

        for (InventoryAction action : this.actions) {
            if (action.execute(this.source)) {
                action.onExecuteSuccess(this.source);
            } else {
                action.onExecuteFail(this.source);
            }
        }

        this.hasExecuted = true;
        return true;
    }

    public boolean hasExecuted() {
        return this.hasExecuted;
    }
}
