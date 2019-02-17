/*package com.nukkitx.api.event.inventory;

public class BrewEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final BlockEntityBrewingStand brewingStand;
    private final Item ingredient;
    private final Item[] potions;
    private final int fuel;
    public BrewEvent(BlockEntityBrewingStand blockEntity) {
        super(blockEntity.getContainer());
        this.brewingStand = blockEntity;
        this.fuel = blockEntity.fuelAmount;

        this.ingredient = blockEntity.getContainer().getIngredient();

        this.potions = new Item[3];
        for (int i = 0; i < 3; i++) {
            this.potions[i] = blockEntity.getContainer().getSlot(i);
        }
    }

    public static HandlerList getHandlers() {
        return handlers;
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
     *//*
    public Item getPotion(int index) {
        return this.potions[index];
    }

    public int getFuel() {
        return fuel;
    }
}*/
