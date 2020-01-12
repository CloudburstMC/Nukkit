package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.level.Position;

public class GrindstoneInventory extends FakeBlockUIComponent {

    public GrindstoneInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.GRINDSTONE, 1, position);
    }

    @Override
    public void close(Player who) {
        onClose(who);
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;
        who.resetCraftingGridType();

        for (int i = 0; i < 3; ++i) {
            this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), this.getItem(i));
            this.clear(i);
        }
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_GRINDSTONE;
    }
}
