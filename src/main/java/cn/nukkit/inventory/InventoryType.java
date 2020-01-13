package cn.nukkit.inventory;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public enum InventoryType {
    CHEST(27, "Chest", 0),
    ENDER_CHEST(27, "Ender Chest", 0),
    DOUBLE_CHEST(27 + 27, "Double Chest", 0),
    PLAYER(40, "Player", -1), //36 CONTAINER, 4 ARMOR
    FURNACE(3, "Furnace", 2),
    CRAFTING(5, "Crafting", 1), //4 CRAFTING slots, 1 RESULT
    WORKBENCH(10, "Crafting", 1), //9 CRAFTING slots, 1 RESULT
    BREWING_STAND(5, "Brewing", 4), //1 INPUT, 3 POTION, 1 fuel
    ANVIL(3, "Anvil", 5), //2 INPUT, 1 OUTPUT
    ENCHANT_TABLE(2, "Enchant", 3), //1 INPUT/OUTPUT, 1 LAPIS
    DISPENSER(0, "Dispenser", 6), //9 CONTAINER
    DROPPER(9, "Dropper", 7), //9 CONTAINER
    HOPPER(5, "Hopper", 8), //5 CONTAINER
    //CAULDRON typeId:9
    //MINECART_CHEST typeId:10
    //MINECART_HOPPER typeId:11
    //MINECART_COMMAND_BLOCK typeId:16
    //HORSE typeId:12
    //JUKEBOX typeId:17
    UI(1, "UI", -1),
    SHULKER_BOX(27, "Shulker Box", 0),
    BEACON(1, "Beacon", 13),
    // 14 is something (crash)
    //COMPOUND_CREATOR typeId:20
    //ELEMENT_CONSTRUCTOR typeId:21
    //MATERIAL_REDUCER typeId:22
    //LAB_TABLE typeId:23
    GRINDSTONE(2, "Grindstone", 26),
    BLAST_FURNACE(3, "Blast Furnace", 27),
    SMOKER(3, "Smoker", 28),
    STONECUTTER(2, "Stonecutter", 29),
    BARREL(27, "Barrel", 0),
    CAMPFIRE(4, "Campfire", -9), // -9 = NONE
    MINECART_CHEST(27, "Minecart with Chest", 0),
    MINECART_HOPPER(5, "Minecart with Hopper", 8);

    private final int size;
    private final String title;
    private final int typeId;

    InventoryType(int defaultSize, String defaultBlockEntity, int typeId) {
        this.size = defaultSize;
        this.title = defaultBlockEntity;
        this.typeId = typeId;
    }

    public int getDefaultSize() {
        return size;
    }

    public String getDefaultTitle() {
        return title;
    }

    public int getNetworkType() {
        return typeId;
    }
}
