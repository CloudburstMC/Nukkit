package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityContainer;
import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CreeperFace
 */
@ToString
public class SlotChangeAction extends InventoryAction {

    protected Inventory inventory;
    private final int inventorySlot;

    public SlotChangeAction(Inventory inventory, int inventorySlot, Item sourceItem, Item targetItem) {
        super(sourceItem, targetItem);
        this.inventory = inventory;
        this.inventorySlot = inventorySlot;
    }

    /**
     * Returns the inventory involved in this action.
     *
     * @return inventory
     */
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Returns the inventorySlot in the inventory which this action modified.
     *
     * @return slot
     */
    public int getSlot() {
        return inventorySlot;
    }

    /**
     * Checks if the item in the inventory at the specified inventorySlot is the same as this action's source item.
     *
     * @param source player
     * @return valid
     */
    public boolean isValid(Player source) {
        if (inventory == null || source.closed) {
            if (Nukkit.DEBUG > 1) source.getServer().getLogger().debug("!isValid null");
            return false;
        }
        if (!source.isCreative() && !(inventory instanceof PlayerUIComponent) && !(inventory instanceof PlayerUIInventory) && !inventory.getViewers().contains(source)) {
            if (Nukkit.DEBUG > 1) source.getServer().getLogger().debug("!isValid viewers " + inventory);
            return false;
        }
        if (inventory instanceof PlayerEnderChestInventory && source.getViewingEnderChest() == null) {
            if (Nukkit.DEBUG > 1) source.getServer().getLogger().debug("!isValid no ec open " + inventory);
            return false;
        }
        if (inventory.getHolder() instanceof BlockEntityContainer) {
            if (((BlockEntity) inventory.getHolder()).closed) {
                if (Nukkit.DEBUG > 1) source.getServer().getLogger().debug("!isValid closed " + inventory);
                return false;
            } else if (((BlockEntity) inventory.getHolder()).chunk == null || ((BlockEntity) inventory.getHolder()).chunk.getProvider() == null) {
                if (Nukkit.DEBUG > 1) source.getServer().getLogger().debug("!isValid chunk provider " + inventory);
                return false;
            } else if (((BlockEntity) inventory.getHolder()).distanceSquared(source) > 4096 || !((BlockEntity) inventory.getHolder()).getLevel().equals(source.getLevel())) {
                if (Nukkit.DEBUG > 1) source.getServer().getLogger().debug("!isValid container distance " + inventory);
                return false;
            }
        }
        if (inventory.getHolder() != source && inventory.getHolder() instanceof Entity) {
            if (((Entity) inventory.getHolder()).closed) {
                if (Nukkit.DEBUG > 1) source.getServer().getLogger().debug("!isValid closed " + inventory);
                return false;
            } else if (((Entity) inventory.getHolder()).chunk == null || ((Entity) inventory.getHolder()).chunk.getProvider() == null) {
                if (Nukkit.DEBUG > 1) source.getServer().getLogger().debug("!isValid chunk provider " + inventory);
                return false;
            } else if (((Entity) inventory.getHolder()).distanceSquared(source) > 4096 || !((Entity) inventory.getHolder()).getLevel().equals(source.getLevel())) {
                if (Nukkit.DEBUG > 1) source.getServer().getLogger().debug("!isValid entity distance " + inventory);
                return false;
            }
        }

        Item check = inventory.getItem(this.inventorySlot);
        boolean valid = (check.getId() == Item.AIR && this.sourceItem.getId() == Item.AIR) || check.equalsExact(this.sourceItem);
        if (!valid && Nukkit.DEBUG > 1) source.getServer().getLogger().debug("!isValid " + inventory);
        return valid;
    }

    /**
     * Sets the item into the target inventory.
     *
     * @param source player
     * @return successfully executed
     */
    public boolean execute(Player source) {
        return this.inventory.setItem(this.inventorySlot, this.targetItem, false);
    }

    /**
     * Sends inventorySlot changes to other viewers of the inventory. This will not send any change back to the source Player.
     *
     * @param source player
     */
    public void onExecuteSuccess(Player source) {
        Set<Player> viewers = new HashSet<>(this.inventory.getViewers());
        viewers.remove(source);

        this.inventory.sendSlot(this.inventorySlot, viewers);

        if (this.inventory instanceof FurnaceInventory && this.inventorySlot == 2) {
            BlockEntityFurnace blockEntityFurnace = ((FurnaceInventory) this.inventory).getHolder();
            if (blockEntityFurnace != null && !blockEntityFurnace.closed) {
                blockEntityFurnace.releaseExperience();
            }
            switch (this.getSourceItemUnsafe().getId()) {
                case Item.IRON_INGOT:
                    source.awardAchievement("acquireIron");
                    break;
                case Item.COOKED_FISH:
                    source.awardAchievement("cookFish");
                    break;
            }
        } else if (this.inventory instanceof BrewingInventory && this.inventorySlot >= 1 && this.inventorySlot <= 3) {
            int itemId = this.getSourceItemUnsafe().getId();
            if (itemId == Item.POTION || itemId == Item.SPLASH_POTION || itemId == Item.LINGERING_POTION) {
                if (this.getSourceItemUnsafe().getDamage() != 0) {
                    source.awardAchievement("potion");
                }
            }
        }
    }

    /**
     * Sends the original inventorySlot contents to the source player to revert the action.
     *
     * @param source player
     */
    public void onExecuteFail(Player source) {
        this.inventory.sendSlot(this.inventorySlot, source);
    }

    @Override
    public void onAddToTransaction(InventoryTransaction transaction) {
        transaction.addInventory(this.inventory);
    }
}
