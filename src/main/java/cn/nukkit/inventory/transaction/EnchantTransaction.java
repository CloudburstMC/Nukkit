package cn.nukkit.inventory.transaction;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.event.inventory.EnchantItemEvent;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.action.EnchantingAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemBookEnchanted;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EnchantTransaction extends InventoryTransaction {

    private Item inputItem;
    private Item outputItem;
    private final List<Item> outputItemCheck = new ArrayList<>();

    private int cost = -1;

    public EnchantTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);

        for (InventoryAction action : actions) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction slotChangeAction = (SlotChangeAction) action;
                if (!(slotChangeAction.getInventory() instanceof EnchantInventory)) {
                    this.outputItemCheck.add(slotChangeAction.getTargetItemUnsafe());
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

        if (this.outputItem == null || this.outputItem.isNull() || this.inputItem == null || this.inputItem.isNull()) {
            return false;
        }

        for (Item check : this.outputItemCheck) {
            if (check != null && !this.outputItem.equals(check)) {
                source.getServer().getLogger().debug("Illegal output");
                return false;
            }
        }

        return this.inputItem.equals(eInv.getInputSlot(), true, true)
                && (this.inputItem.getId() == this.outputItem.getId() || (this.inputItem.getId() == Item.BOOK && this.outputItem.getId() == Item.ENCHANTED_BOOK))
                && (this.inputItem.getCount() == this.outputItem.getCount() || (this.outputItem.getId() == Item.ENCHANTED_BOOK && this.outputItem.getCount() == 1)
                && validateNBT());
    }

    private boolean validateNBT() {
        if (!(outputItem instanceof ItemTool || outputItem instanceof ItemArmor || outputItem instanceof ItemBookEnchanted)) {
            source.getServer().getLogger().debug("Non-enchantable item");
            return false;
        }

        for (Enchantment e : outputItem.getEnchantments()) {
            if (e.isTreasure()) {
                source.getServer().getLogger().debug("Illegal treasure enchantment");
                return false;
            }
        }

        CompoundTag a = this.inputItem.getNamedTag();
        a = a == null ? new CompoundTag() : a.clone().remove("ench");
        CompoundTag b = this.outputItem.getNamedTag();
        b = b == null ? new CompoundTag() : b.clone().remove("ench");
        if (!a.equals(b)) {
            if (Nukkit.DEBUG > 1) {
                source.getServer().getLogger().debug("NBT check failed: input=" + a + ", output=" + b);
            }
            return false;
        }

        return true;
    }

    @Override
    public boolean execute() {
        // This will validate the enchant conditions
        if (this.invalid || this.hasExecuted() || !this.canExecute()) {
            source.removeAllWindows(false);
            this.sendInventories();
            return false;
        }

        EnchantInventory inv = (EnchantInventory) getSource().getWindowById(Player.ENCHANT_WINDOW_ID);
        EnchantItemEvent ev = new EnchantItemEvent(inv, inputItem, outputItem, cost, source);
        source.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            this.sendInventories();
            source.setNeedSendInventory(true);
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

    @Override
    public boolean checkForItemPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof EnchantingAction) return true;
        }
        return false;
    }
}
