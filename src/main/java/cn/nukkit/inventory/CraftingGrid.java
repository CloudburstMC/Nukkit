package cn.nukkit.inventory;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftingGrid extends PlayerUIComponent {

    CraftingGrid(PlayerUIInventory playerUI) {
        this(playerUI, 28, 4);
    }

    CraftingGrid(PlayerUIInventory playerUI, int offset, int size) {
        super(playerUI, offset, size);
    }
}
