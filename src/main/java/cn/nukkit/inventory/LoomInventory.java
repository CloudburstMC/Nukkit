package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

public class LoomInventory extends FakeBlockUIComponent {

    public static final int OFFSET = 9;

    public LoomInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.LOOM, OFFSET, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_LOOM;
    }

    public Item getFirstItem() {
        return getItem(0);
    }

    public Item getSecondItem() {
        return getItem(1);
    }

    public void setFirstItem(Item item) {
        this.setItem(0, item);
    }

    public void setSecondItem(Item item) {
        this.setItem(1, item);
    }
}
