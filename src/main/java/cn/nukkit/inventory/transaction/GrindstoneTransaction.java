package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.GrindItemEvent;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.action.GrindstoneItemAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GrindstoneTransaction extends InventoryTransaction {

    @Getter
    private Item equipmentItem;
    @Getter
    private Item ingredientItem;
    @Getter
    private Item outputItem;
    private final List<Item> outputItemCheck = new ArrayList<>();

    public GrindstoneTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);

        for (InventoryAction action : actions) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction slotChangeAction = (SlotChangeAction) action;
                if (!(slotChangeAction.getInventory() instanceof GrindstoneInventory)) {
                    this.outputItemCheck.add(slotChangeAction.getTargetItemUnsafe());
                }
            }
        }
    }

    @Override
    public void addAction(InventoryAction action) {
        super.addAction(action);
        if (action instanceof GrindstoneItemAction) {
            switch (((GrindstoneItemAction) action).getType()) {
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT:
                    this.equipmentItem = action.getTargetItem();
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL:
                    this.ingredientItem = action.getTargetItem();
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT:
                    this.outputItem = action.getSourceItem();
                    break;
            }
        }
    }

    @Override
    public boolean canExecute() {
        Inventory inventory = getSource().getWindowById(Player.GRINDSTONE_WINDOW_ID);
        if (!(inventory instanceof GrindstoneInventory)) {
            return false;
        }

        GrindstoneInventory grindstoneInventory = (GrindstoneInventory) inventory;

        if (this.outputItem == null || this.outputItem.isNull() || this.equipmentItem == null || this.equipmentItem.isNull()) {
            return false;
        }

        for (Item check : this.outputItemCheck) {
            if (check != null && !this.outputItem.equals(check)) {
                source.getServer().getLogger().debug("Illegal output");
                return false;
            }
        }

        Item ingredientOptional = ingredientItem != null ? ingredientItem : Item.get(Item.AIR);

        return ingredientOptional.equals(grindstoneInventory.getIngredient(), true, true)
                && equipmentItem.equals(grindstoneInventory.getEquipment(), true, true)
                && outputItem.equals(grindstoneInventory.getResult(), true, true);
    }

    @Override
    public boolean execute() {
        if (this.invalid || this.hasExecuted() || !this.canExecute()) {
            this.source.removeAllWindows(false);
            this.sendInventories();
            return false;
        }

        GrindstoneInventory inventory = (GrindstoneInventory) getSource().getWindowById(Player.GRINDSTONE_WINDOW_ID);
        GrindItemEvent event = new GrindItemEvent(inventory, this.equipmentItem, this.outputItem, this.ingredientItem, this.source);
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

        return true;
    }

    public static boolean isIn(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof GrindstoneItemAction) return true;
        }
        return false;
    }

    @Override
    public boolean checkForItemPart(List<InventoryAction> actions) {
        return isIn(actions);
    }
}
