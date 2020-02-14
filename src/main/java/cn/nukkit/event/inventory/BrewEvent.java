package cn.nukkit.event.inventory;

import cn.nukkit.blockentity.BrewingStand;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * @author CreeperFace
 */
public class BrewEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BrewingStand brewingStand;
    private final Item ingredient;
    private final Item[] potions;
    private final int fuel;

    public BrewEvent(BrewingStand blockEntity) {
        super(blockEntity.getInventory());
        this.brewingStand = blockEntity;
        this.fuel = blockEntity.getFuelAmount();

        this.ingredient = blockEntity.getInventory().getIngredient();

        this.potions = new Item[3];
        for (int i = 0; i < 3; i++) {
            this.potions[i] = blockEntity.getInventory().getItem(i);
        }
    }

    public BrewingStand getBrewingStand() {
        return brewingStand;
    }

    public Item getIngredient() {
        return ingredient;
    }

    public Item[] getPotions() {
        return potions;
    }

    /**
     * @param index Potion index in range 0 - 2
     * @return potion
     */
    public Item getPotion(int index) {
        return this.potions[index];
    }

    public int getFuel() {
        return fuel;
    }
}
