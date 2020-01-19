package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.level.Position;

public class StonecutterInventory extends FakeBlockUIComponent {
    public StonecutterInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.STONECUTTER, 3, position);
    }
    
    @Override
    public void onOpen(Player who) {
        who.craftingType = Player.CRAFTING_STONECUTTER;
        sendContents(who);
    }
}
