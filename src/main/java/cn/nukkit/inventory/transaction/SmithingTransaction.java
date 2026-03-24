package cn.nukkit.inventory.transaction;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.event.inventory.SmithItemEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.SmithingInventory;
import cn.nukkit.inventory.transaction.action.CreativeInventoryAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.inventory.transaction.action.SmithingItemAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

import java.util.List;

/**
 * @author joserobjr
 */
public class SmithingTransaction extends InventoryTransaction {

    private Item equipmentItem;
    private Item ingredientItem;
    private Item outputItem;
    protected Item templateItem;

    public SmithingTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);
    }

    @Override
    public void addAction(InventoryAction action) {
        if (action instanceof SmithingItemAction) {
            switch (((SmithingItemAction) action).getType()) {
                case 0: // input
                    if (this.equipmentItem != null) {
                        this.invalid = true;
                        source.getServer().getLogger().debug("Duplicate addAction for equipmentItem");
                        return;
                    }
                    this.equipmentItem = action.getTargetItem();
                    break;
                case 1: // ingredient
                    if (this.ingredientItem != null) {
                        this.invalid = true;
                        source.getServer().getLogger().debug("Duplicate addAction for ingredientItem");
                        return;
                    }
                    this.ingredientItem = action.getTargetItem();
                    break;
                case 2: // result
                    if (this.outputItem != null) {
                        this.invalid = true;
                        source.getServer().getLogger().debug("Duplicate addAction for outputItem");
                        return;
                    }
                    this.outputItem = action.getSourceItem();
                    break;
                case 3: // template
                    if (this.templateItem != null && !this.templateItem.equals(action.getTargetItemUnsafe())) { // "hack"
                        this.invalid = true;
                        source.getServer().getLogger().debug("Duplicate addAction for templateItem");
                        return;
                    }
                    this.templateItem = action.getTargetItem();
                    break;
            }
        } else if (action instanceof CreativeInventoryAction) {
            CreativeInventoryAction creativeAction = (CreativeInventoryAction) action;
            if (creativeAction.getActionType() == 0
                    && creativeAction.getSourceItemUnsafe().isNull()
                    && !creativeAction.getTargetItemUnsafe().isNull() && creativeAction.getTargetItemUnsafe().getId() == ItemID.NETHERITE_INGOT) {
                if (this.ingredientItem != null) {
                    this.invalid = true;
                    source.getServer().getLogger().debug("Duplicate addAction for ingredientItem");
                    return;
                }
                this.ingredientItem = action.getTargetItem();
            }
        }
        super.addAction(action);
    }

    @Override
    public boolean canExecute() {
        if (!super.canExecute()) {
            return false;
        }

        Inventory inventory = getSource().getWindowById(Player.SMITHING_WINDOW_ID);
        if (!(inventory instanceof SmithingInventory)) {
            return false;
        }

        SmithingInventory smithingInventory = (SmithingInventory) inventory;

        if (this.outputItem == null || this.outputItem.isNull() || this.equipmentItem == null || this.equipmentItem.isNull()) {
            return false;
        }

        for (InventoryAction action : actions) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction slotChangeAction = (SlotChangeAction) action;
                if (!(slotChangeAction.getInventory() instanceof SmithingInventory)) {
                    Item item = slotChangeAction.getTargetItemUnsafe();
                    if (item != null && !item.isNull() && !this.outputItem.equals(item)) {
                        this.invalid = true;
                        if (Nukkit.DEBUG > 1) {
                            source.getServer().getLogger().debug("Illegal output " + item);
                        }
                        return false;
                    }
                }
            }
        }

        Item air = Item.get(0);
        Item ingredient = ingredientItem != null ? ingredientItem : air;
        Item template = templateItem != null ? templateItem : air;

        return equipmentItem.equals(smithingInventory.getEquipment(), true, true)
                && ingredient.equals(smithingInventory.getIngredient(), true, true)
                && template.equals(smithingInventory.getTemplate(), true, true)
                && outputItem.equals(smithingInventory.getResult(), true, true);
    }

    @Override
    public boolean execute() {
        if (this.hasExecuted() || !this.canExecute() || this.invalid) {
            this.source.removeAllWindows(false);
            this.sendInventories();
            return false;
        }

        SmithingInventory inventory = (SmithingInventory) getSource().getWindowById(Player.SMITHING_WINDOW_ID);
        SmithItemEvent event = new SmithItemEvent(inventory, this.equipmentItem, this.outputItem, this.ingredientItem, this.source);
        this.source.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.sendInventories();
            source.setNeedSendInventory(true);
            return true;
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

    public Item getInputItem() {
        return this.equipmentItem;
    }

    public Item getMaterialItem() {
        return this.ingredientItem;
    }

    public Item getOutputItem() {
        return this.outputItem;
    }

    public static boolean isIn(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof SmithingItemAction) return true;
        }
        return false;
    }

    @Override
    public boolean checkForItemPart(List<InventoryAction> actions) {
        return isIn(actions);
    }
}
