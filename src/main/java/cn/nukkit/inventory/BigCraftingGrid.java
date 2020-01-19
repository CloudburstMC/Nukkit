package cn.nukkit.inventory;

/**
 * @author CreeperFace
 */
public class BigCraftingGrid extends CraftingGrid {
    BigCraftingGrid(PlayerUIInventory playerUI) {
        super(playerUI, 32, 9);
    }
    
    @Override
    public InventoryType getType() {
        return InventoryType.WORKBENCH;
    }
}
