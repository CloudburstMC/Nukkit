package cn.nukkit.inventory.transaction;

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

import java.util.ArrayList;
import java.util.List;

/**
 * @author joserobjr
 */
public class SmithingTransaction extends InventoryTransaction {

    private Item equipmentItem;
    private Item ingredientItem;
    private Item outputItem;
    private Item templateItem;
    private final List<Item> outputItemCheck = new ArrayList<>();

    public SmithingTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);

        for (InventoryAction action : actions) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction slotChangeAction = (SlotChangeAction) action;
                if (!(slotChangeAction.getInventory() instanceof SmithingInventory)) {
                    this.outputItemCheck.add(slotChangeAction.getTargetItemUnsafe());
                }
            }
        }
    }

    @Override
    public void addAction(InventoryAction action) {
        super.addAction(action);
        if (action instanceof SmithingItemAction) {
            switch (((SmithingItemAction) action).getType()) {
                case 0: // input
                    this.equipmentItem = action.getTargetItem();
                    break;
                case 1: // ingredient
                    this.ingredientItem = action.getTargetItem();
                    break;
                case 2: // result
                    this.outputItem = action.getSourceItem();
                    break;
                case 3: // template
                    this.templateItem = action.getTargetItem();
                    break;
            }
        } else if (action instanceof CreativeInventoryAction) {
            CreativeInventoryAction creativeAction = (CreativeInventoryAction) action;
            if (creativeAction.getActionType() == 0
                    && creativeAction.getSourceItemUnsafe().isNull()
                    && !creativeAction.getTargetItemUnsafe().isNull() && creativeAction.getTargetItemUnsafe().getId() == ItemID.NETHERITE_INGOT) {
                this.ingredientItem = action.getTargetItem();
            }
        }
    }

    @Override
    public boolean canExecute() {
        Inventory inventory = getSource().getWindowById(Player.SMITHING_WINDOW_ID);
        if (!(inventory instanceof SmithingInventory)) {
            return false;
        }

        SmithingInventory smithingInventory = (SmithingInventory) inventory;

        if (this.outputItem == null || this.outputItem.isNull() || this.equipmentItem == null || this.equipmentItem.isNull()) {
            return false;
        }

        for (Item check : this.outputItemCheck) {
            if (check != null && !check.isNull() && !this.outputItem.equals(check)) {
                source.getServer().getLogger().debug("Illegal output");
                return false;
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
        if (this.invalid || this.hasExecuted() || !this.canExecute()) {
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

        if (inventory != null) {
            inventory.sendContents(source);
        }
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
