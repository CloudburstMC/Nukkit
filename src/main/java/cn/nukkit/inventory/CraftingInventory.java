package cn.nukkit.inventory;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftingInventory extends BaseInventory {

    private Inventory resultInventory;

    public CraftingInventory(InventoryHolder holder, Inventory resultInventory, InventoryType type) {
        super(holder, type);
        if (!type.getDefaultTitle().equals("Crafting")) {
            throw new IllegalStateException("Invalid Inventory type, expected CRAFTING or WORKBENCH");
        }
        this.resultInventory = resultInventory;
    }

    public Inventory getResultInventory() {
        return resultInventory;
    }

    @Override
    public int getSize() {
        return this.getResultInventory().getSize() + super.getSize();
    }
}
