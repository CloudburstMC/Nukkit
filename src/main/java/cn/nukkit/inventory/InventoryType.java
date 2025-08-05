package cn.nukkit.inventory;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public enum InventoryType {

    CHEST(27, "Chest", 0), //27 CONTAINER
    ENDER_CHEST(27, "Ender Chest", 0), //27 CONTAINER
    DOUBLE_CHEST(54, "Double Chest", 0), //27 + 27 CONTAINER
    PLAYER(40, "Player", -1), //36 CONTAINER, 4 ARMOR
    FURNACE(3, "Furnace", 2), //1 INPUT/OUTPUT, 1 FUEL
    BLAST_FURNACE(3, "Blast Furnace", 27), //1 INPUT/OUTPUT, 1 FUEL
    SMOKER(3, "Smoker", 28), //1 INPUT/OUTPUT, 1 FUEL
    CAMPFIRE(4, "Campfire", -1), //4 CONTAINER
    CRAFTING(5, "Crafting", 1), //4 CRAFTING SLOTS, 1 RESULT
    WORKBENCH(10, "Crafting", 1), //9 CRAFTING SLOTS, 1 RESULT
    BREWING_STAND(5, "Brewing", 4), //1 INPUT, 3 POTION, 1 FUEL
    ANVIL(3, "Anvil", 5), //2 INPUT, 1 OUTPUT
    ENCHANT_TABLE(2, "Enchantment Table", 3), //1 INPUT/OUTPUT, 1 LAPIS
    DISPENSER(9, "Dispenser", 6), //9 CONTAINER
    DROPPER(9, "Dropper", 7), //9 CONTAINER
    HOPPER(5, "Hopper", 8), //5 CONTAINER
    UI(1, "UI", -1), //1 CONTAINER
    SHULKER_BOX(27, "Shulker Box", 0), //27 CONTAINER
    BEACON(1, "Beacon", 13), //1 INPUT
    ENTITY_ARMOR(4, "Entity Armor", -1), //4 ARMOR
    ENTITY_EQUIPMENT(36, "Entity Equipment", -1), //36 CONTAINER
    MINECART_CHEST(27, "Minecart with Chest", 0), //27 CONTAINER
    MINECART_HOPPER(5, "Minecart with Hopper", 8), //5 CONTAINER
    OFFHAND(1, "Offhand", -1), //1 CONTAINER
    TRADING(3, "Villager Trade", 15), //3 CONTAINER
    BARREL(27, "Barrel", 0), //27 CONTAINER
    LOOM(4, "Loom", 24), //4 CONTAINER
    CHEST_BOAT(27, "Boat with Chest", 0), //27 CONTAINER
    DONKEY(15, "Donkey", 12), //15 CONTAINER
    SMITHING_TABLE(3, "Smithing Table", 33),
    LECTERN(0, "Lectern", 25),
    GRINDSTONE(3, "Grindstone", 26);

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
