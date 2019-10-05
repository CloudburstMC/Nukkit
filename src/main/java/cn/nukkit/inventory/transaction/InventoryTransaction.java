package cn.nukkit.inventory.transaction;

import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;
import cn.nukkit.utils.MainLogger;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author CreeperFace
 */
public class InventoryTransaction {

    private long creationTime;
    protected boolean hasExecuted;

    protected Player source;

    protected Set<Inventory> inventories = new HashSet<>();

    protected Set<InventoryAction> actions = new HashSet<>();

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

    public Set<InventoryAction> getActions() {
        return actions;
    }

    public void addAction(InventoryAction action) {
        if (!this.actions.contains(action)) {
            this.actions.add(action);
            action.onAddToTransaction(this);
        } else {
            throw new RuntimeException("Tried to add the same action to a transaction twice");
        }
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
        for (Inventory inventory : this.inventories) {
            inventory.sendContents(this.source);
            if (inventory instanceof PlayerInventory) {
                ((PlayerInventory) inventory).sendArmorContents(this.source);
            }
        }
    }

    /**
     * Iterates over SlotChangeActions in this transaction and compacts any which refer to the same inventorySlot in the same
     * inventory so they can be correctly handled.
     * <p>
     * Under normal circumstances, the same inventorySlot would never be changed more than once in a single transaction. However,
     * due to the way things like the crafting grid are "implemented" in MCPE 1.2 (a.k.a. hacked-in), we may get
     * multiple inventorySlot changes referring to the same inventorySlot in a single transaction. These multiples are not even guaranteed
     * to be in the correct order (inventorySlot splitting in the crafting grid for example, causes the actions to be sent in the
     * wrong order), so this method also tries to chain them into order.
     * </p>
     *
     * @return successful
     */
    protected boolean squashDuplicateSlotChanges() {
        Map<Integer, List<SlotChangeAction>> slotChanges = new HashMap<>();

        for (InventoryAction action : this.actions) {
            if (action instanceof SlotChangeAction) {
                int hash = Objects.hash(((SlotChangeAction) action).getInventory(), ((SlotChangeAction) action).getSlot());

                List<SlotChangeAction> list = slotChanges.get(hash);
                if (list == null) {
                    list = new ArrayList<>();
                }

                list.add((SlotChangeAction) action);

                slotChanges.put(hash, list);
            }
        }

        for (Entry<Integer, List<SlotChangeAction>> entry : new ArrayList<>(slotChanges.entrySet())) {
            int hash = entry.getKey();
            List<SlotChangeAction> list = entry.getValue();

            if (list.size() == 1) { //No need to compact inventorySlot changes if there is only one on this inventorySlot
                slotChanges.remove(hash);
                continue;
            }

            List<SlotChangeAction> originalList = new ArrayList<>(list);

            SlotChangeAction originalAction = null;
            Item lastTargetItem = null;

            for (int i = 0; i < list.size(); i++) {
                SlotChangeAction action = list.get(i);

                if (action.isValid(this.source)) {
                    originalAction = action;
                    lastTargetItem = action.getTargetItem();
                    list.remove(i);
                    break;
                }
            }

            if (originalAction == null) {
                return false; //Couldn't find any actions that had a source-item matching the current inventory inventorySlot
            }

            int sortedThisLoop;

            do {
                sortedThisLoop = 0;
                for (int i = 0; i < list.size(); i++) {
                    SlotChangeAction action = list.get(i);

                    Item actionSource = action.getSourceItem();
                    if (actionSource.equalsExact(lastTargetItem)) {
                        lastTargetItem = action.getTargetItem();
                        list.remove(i);
                        sortedThisLoop++;
                    }
                    else if (actionSource.equals(lastTargetItem)) {
                        lastTargetItem.count -= actionSource.count;
                        list.remove(i);
                        if (lastTargetItem.count == 0) sortedThisLoop++;
                    }
                }
            } while (sortedThisLoop > 0);

            if (list.size() > 0) { //couldn't chain all the actions together
                MainLogger.getLogger().debug("Failed to compact " + originalList.size() + " actions for " + this.source.getName());
                return false;
            }

            for (SlotChangeAction action : originalList) {
                this.actions.remove(action);
            }

            this.addAction(new SlotChangeAction(originalAction.getInventory(), originalAction.getSlot(), originalAction.getSourceItem(), lastTargetItem));

            MainLogger.getLogger().debug("Successfully compacted " + originalList.size() + " actions for " + this.source.getName());
        }

        return true;
    }

    public boolean canExecute() {
        this.squashDuplicateSlotChanges();

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

            if (slotChange.getInventory() instanceof PlayerInventory) {
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
