package cn.nukkit.api.inventory;

/**
 * Inventory types that are supported
 */
public enum InventoryType {
    /**
     * Single chest
     */
    CHEST(27),

    /**
     * Ender chest
     */
    ENDER_CHEST(27),

    /**
     * Double chest equivalent to two chests
     */
    DOUBLE_CHEST(27 + 27),

    /**
     * Player inventory
     */
    PLAYER(36),

    /**
     * Furnace inventory
     */
    FURNACE(3),

    /**
     * 2 x 2 crafting grid inventory
     */
    CRAFTING(5),

    /**
     * Crafting table inventory
     */
    WORKBENCH(10),

    /**
     * Brewing stand inventory
     */
    BREWING_STAND(5),

    /**
     * Anvil inventory
     */
    ANVIL(3),

    /**
     * Enchantment table inventory
     */
    ENCHANTMENT_TABLE(2),

    /**
     * Dispenser inventory
     */
    DISPENSER(9),

    /**
     * Dropper inventory
     */
    DROPPER(9),

    /**
     * Hopper inventory
     */
    HOPPER(5);

    private final byte size;

    InventoryType(int size) {
        this.size = (byte) size;
    }

    public byte getSize() {
        return size;
    }
}
