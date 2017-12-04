package cn.nukkit.server.event.inventory;

import cn.nukkit.server.blockentity.BlockEntityBrewingStand;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.item.Item;

/**
 * @author CreeperFace
 */
public class BrewEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BlockEntityBrewingStand brewingStand;
    private final Item ingredient;
    private final Item[] potions;
    private final int fuel;

    public BrewEvent(BlockEntityBrewingStand blockEntity) {
        super(blockEntity.getInventory());
        this.brewingStand = blockEntity;
        this.fuel = blockEntity.fuelAmount;

        this.ingredient = blockEntity.getInventory().getIngredient();

        this.potions = new Item[3];
        for (int i = 0; i < 3; i++) {
            this.potions[i] = blockEntity.getInventory().getItem(i);
        }
    }

    public BlockEntityBrewingStand getBrewingStand() {
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
     */
    public Item getPotion(int index) {
        return this.potions[index];
    }

    public int getFuel() {
        return fuel;
    }
}
