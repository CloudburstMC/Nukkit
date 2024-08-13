package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.EnchantItemEvent;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.action.EnchantingAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EnchantTransaction extends InventoryTransaction {

    private Item inputItem;
    private Item outputItem;
    private Item outputItemCheck;

    private int cost = -1;

    public EnchantTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);

        for (InventoryAction action : actions) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction slotChangeAction = (SlotChangeAction) action;
                if (slotChangeAction.getInventory() instanceof EnchantInventory && slotChangeAction.getSlot() == 0) {
                    this.outputItemCheck = slotChangeAction.getTargetItem();
                }
            }
        }
    }

    @Override
    public boolean canExecute() {
        Inventory inv = getSource().getWindowById(Player.ENCHANT_WINDOW_ID);
        if (!(inv instanceof EnchantInventory)) {
            return false;
        }
        EnchantInventory eInv = (EnchantInventory) inv;
        if (!getSource().isCreative()) {
            if (this.cost < 1) {
                return false;
            } else {
                Item reagent = eInv.getReagentSlot();
                if (reagent.count < this.cost || !reagent.equals(Item.get(Item.DYE, 4), true, false)) {
                    return false;
                }
            }
        }
        return this.inputItem != null && this.outputItem != null
                && this.inputItem.equals(eInv.getInputSlot(), true, true)
                && (this.outputItemCheck == null || this.inputItem.getId() == this.outputItemCheck.getId() ||
                (this.inputItem.getId() == Item.BOOK && this.outputItemCheck.getId() == Item.ENCHANTED_BOOK))
                && (this.outputItemCheck == null || this.inputItem.getCount() == this.outputItemCheck.getCount() ||
                (this.outputItemCheck.getId() == Item.ENCHANTED_BOOK && this.outputItemCheck.getCount() == 1));
    }

    @Override
    public boolean execute() {
        // This will validate the enchant conditions
        if (this.hasExecuted || !this.canExecute()) {
            source.removeAllWindows(false);
            this.sendInventories();
            return false;
        }
        EnchantInventory inv = (EnchantInventory) getSource().getWindowById(Player.ENCHANT_WINDOW_ID);
        EnchantItemEvent ev = new EnchantItemEvent(inv, inputItem, outputItem, cost, source);
        source.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            source.removeAllWindows(false);
            this.sendInventories();
            // Cancelled by plugin, means handled OK
            return true;
        }
        // This will process all the slot changes
        for (InventoryAction a : this.actions) {
            if (a.execute(source)) {
                a.onExecuteSuccess(source);
            } else {
                a.onExecuteFail(source);
            }
        }

        if (!ev.getNewItem().equals(this.outputItem, true, true)) {
            // Plugin changed item, so the previous slot change is going to be invalid
            // Send the replaced item to the enchant inventory manually
            inv.setItem(0, ev.getNewItem(), true);
        }

        if (!source.isCreative()) {
            source.setExperience(source.getExperience(), source.getExperienceLevel() - ev.getXpCost());
        }
        return true;
    }

    @Override
    public void addAction(InventoryAction action) {
        super.addAction(action);
        if (action instanceof EnchantingAction) {
            switch (((EnchantingAction) action).getType()) {
                case NetworkInventoryAction.SOURCE_TYPE_ENCHANT_INPUT:
                    this.inputItem = action.getTargetItem(); // Input sent as newItem
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ENCHANT_OUTPUT:
                    this.outputItem = action.getSourceItem(); // Output sent as oldItem
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ENCHANT_MATERIAL:
                    if (action.getTargetItemUnsafe().getId() == Item.AIR) {
                        this.cost = action.getSourceItemUnsafe().count;
                    } else {
                        this.cost = action.getSourceItemUnsafe().count - action.getTargetItemUnsafe().count;
                    }
                    break;
            }

        }
    }

    public boolean checkForEnchantPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof EnchantingAction) return true;
        }
        return false;
    }
}
