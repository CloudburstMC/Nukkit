package com.nukkitx.server.block.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author CreeperFace
 */

@AllArgsConstructor
@Getter
public enum BlockEntityType {
    CHEST("Chest"),
    ENDER_CHEST("EnderChest"),
    TRAPPED_CHEST("TrappedChest"),
    FURNACE("Furnace"),
    SIGN("Sign"),
    MOB_SPAWNER("MobSpawner"),
    ENCHANT_TABLE("EnchantTable"),
    SKULL("Skull"),
    FLOWER_POT("FlowerPot"),
    BREWING_STAND("BrewingStand"),
    DAYLIGHT_DETECTOR("DaylightSensor"),
    MUSIC("Music"),
    ITEM_FRAME("ItemFrame"),
    CAULDRON("Cauldron"),
    BEACON("Beacon"),
    PISTON_ARM("PistonArm"),
    MOVING_BLOCK("MovingBlock"),
    COMPARATOR("Comparator"),
    HOPPER("Hopper"),
    BED("Bed"),
    JUKEBOX("Jukebox"),
    SHULKER_BOX("ShulkerBox"),
    BANNER("Banner"),
    DISPENSER("Dispenser"),
    NOTE_BLOCK("NoteBlock"),
    CONDUIT("Conduit")
    //TODO
    ;

    private final String type;
}
