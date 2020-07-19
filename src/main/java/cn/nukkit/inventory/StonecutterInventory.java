package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

public class StonecutterInventory extends FakeBlockUIComponent {
    public StonecutterInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.STONECUTTER, 3, position);
    }
    
    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;

        Item[] drops = who.getInventory().addItem(this.getItem(0));
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), drop);
            }
        }

        this.clear(0);
        who.resetCraftingGridType();
    }
}
