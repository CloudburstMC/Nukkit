package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.RepairItemEvent;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.action.RepairItemAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDurable;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;

import java.util.List;

public class RepairItemTransaction extends InventoryTransaction {

    private Item inputItem;
    private Item materialItem;
    private Item outputItem;

    private int cost;

    public RepairItemTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);
    }

    @Override
    public boolean canExecute() {
        Inventory inventory = getSource().getWindowById(Player.ANVIL_WINDOW_ID);
        if (inventory == null) {
            return false;
        }
        AnvilInventory anvilInventory = (AnvilInventory) inventory;
        this.cost = anvilInventory.getCost();
        return this.inputItem != null && this.outputItem != null && this.inputItem.equals(anvilInventory.getInputSlot(), true, true)
                && (!this.hasMaterial() || this.materialItem.equals(anvilInventory.getMaterialSlot(), true, true))
                && this.checkRecipeValid() && this.checkRenameValid();
    }

    @Override
    public boolean execute() {
        if (this.hasExecuted() || !this.canExecute()) {
            this.source.removeAllWindows(false);
            this.sendInventories();
            return false;
        }
        AnvilInventory inventory = (AnvilInventory) getSource().getWindowById(Player.ANVIL_WINDOW_ID);

        RepairItemEvent event = new RepairItemEvent(inventory, this.inputItem, this.outputItem, this.materialItem, this.cost, this.source);
        this.source.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.source.removeAllWindows(false);
            this.sendInventories();
            return true;
        }

        for (InventoryAction action : this.actions) {
            if (action.execute(this.source)) {
                action.onExecuteSuccess(this.source);
            } else {
                action.onExecuteFail(this.source);
            }
        }

        if (!this.source.isCreative()) {
            this.source.setExperience(this.source.getExperience(), this.source.getExperienceLevel() - event.getCost());
        }
        this.source.getLevel().addLevelSoundEvent(this.source, LevelSoundEventPacket.SOUND_RANDOM_ANVIL_USE);
        return true;
    }

    @Override
    public void addAction(InventoryAction action) {
        super.addAction(action);
        if (action instanceof RepairItemAction) {
            switch (((RepairItemAction) action).getType()) {
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT:
                    this.inputItem = action.getTargetItem();
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT:
                    this.outputItem = action.getSourceItem();
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL:
                    this.materialItem = action.getTargetItem();
                    break;
            }
        }
    }

    private boolean checkRecipeValid() {
        if (this.cost < 0 || this.outputItem.getRepairCost() < this.inputItem.getRepairCost()) {
            return false;
        }
        if (this.isMapRecipe()) {
            return this.matchMapRecipe();
        } else if (this.isEnchantedBookCombine()) {
            return this.matchEnchantedBookRecipe();
        }
        //TODO: Check item has valid enchantments
        return (this.hasMaterial() && (!(this.materialItem instanceof ItemDurable) || this.materialItem.getId() == this.inputItem.getId()
                && this.materialItem.getCount() == 1 && this.inputItem.getCount() == 1) || this.inputItem.equals(this.outputItem, true, false)
                && this.inputItem.getCount() == this.outputItem.getCount()) && (this.cost < 40 || this.source.isCreative());
    }

    private boolean hasMaterial() {
        return this.materialItem != null && !this.materialItem.isNull();
    }

    private boolean checkRenameValid() {
        return this.inputItem.getCustomName().equals(this.outputItem.getCustomName())
                || this.outputItem.getCustomName().length() <= 30 && (this.cost > 0 || this.source.isCreative());
    }

    private boolean isEnchantedBookCombine() {
        return this.inputItem.getId() == Item.ENCHANTED_BOOK && this.outputItem.getId() == Item.ENCHANTED_BOOK
                && this.hasMaterial() && this.materialItem.getId() == Item.ENCHANTED_BOOK;
    }

    private boolean matchEnchantedBookRecipe() {
        if (!this.materialItem.hasEnchantments()) {
            return false;
        }

        for (Enchantment ench : this.inputItem.getEnchantments()) {
            if (this.outputItem.getEnchantment(ench.getId()) == null) {
                return false;
            }
        }
        for (Enchantment ench : this.materialItem.getEnchantments()) {
            if (this.outputItem.getEnchantment(ench.getId()) == null) {
                return false;
            }
        }

        boolean combine = false;
        for (Enchantment ench : this.outputItem.getEnchantments()) {
            if (ench.getLevel() > ench.getMaxLevel()) {
                return false;
            }
            Enchantment inputEnch = this.inputItem.getEnchantment(ench.getId());
            Enchantment materialEnch = this.materialItem.getEnchantment(ench.getId());
            if (inputEnch == null && materialEnch == null) {
                return false;
            } else if (inputEnch != null && materialEnch != null) {
                if (inputEnch.getLevel() > materialEnch.getLevel()) {
                    //return false;
                } else if (inputEnch.getLevel() < materialEnch.getLevel()) {
                    if (ench.getLevel() != materialEnch.getLevel()) {
                        return false;
                    }
                    combine = true;
                } else if (inputEnch.getLevel() == materialEnch.getLevel() && inputEnch.getLevel() + 1 <= ench.getMaxLevel()) {
                    if (ench.getLevel() != inputEnch.getLevel() + 1) {
                        return false;
                    }
                    combine = true;
                }
            } else if (inputEnch != null) {
                if (inputEnch.getLevel() != ench.getLevel()) {
                    return false;
                }
                combine = true;
            } else {
                if (materialEnch.getLevel() != ench.getLevel()) {
                    return false;
                }
                combine = true;
            }
        }
        return combine;
    }

    private boolean isMapRecipe() {
        return this.hasMaterial() && (this.inputItem.getId() == Item.MAP || this.inputItem.getId() == Item.EMPTY_MAP)
                && (this.materialItem.getId() == Item.EMPTY_MAP || this.materialItem.getId() == Item.PAPER || this.materialItem.getId() == Item.COMPASS);
    }

    private boolean matchMapRecipe() {
        if (this.inputItem.getId() == Item.EMPTY_MAP) {
            return this.inputItem.getDamage() != 2 && this.materialItem.getId() == Item.COMPASS // locator
                    && this.outputItem.getId() == Item.EMPTY_MAP && this.outputItem.getDamage() == 2 && this.outputItem.getCount() == 1;
        } else if (this.inputItem.getId() == Item.MAP) {
            if (this.materialItem.getId() == Item.COMPASS) { // locator
                return this.inputItem.getDamage() < 2 && this.outputItem.getId() == Item.MAP
                        && this.outputItem.getDamage() == 2 && this.outputItem.getCount() == 1;
            } else if (this.materialItem.getId() == Item.EMPTY_MAP) { // clone
                return this.outputItem.getId() == Item.MAP && this.outputItem.getDamage() == this.inputItem.getDamage() && this.outputItem.getCount() == 2;
            } else if (this.materialItem.getId() == Item.PAPER && this.materialItem.getCount() >= 8) { // zoom out
                return this.inputItem.getDamage() < 3 && this.outputItem.getId() == Item.MAP
                        && this.outputItem.getDamage() == this.inputItem.getDamage() && this.outputItem.getCount() == 1;
            }
        }
        return false;
    }

    public Item getInputItem() {
        return this.inputItem;
    }

    public Item getMaterialItem() {
        return this.materialItem;
    }

    public Item getOutputItem() {
        return this.outputItem;
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public static boolean checkForRepairItemPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof RepairItemAction) {
                return true;
            }
        }
        return false;
    }
}
