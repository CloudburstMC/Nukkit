package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.level.Position;


/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantInventory extends FakeBlockUIComponent {

    public EnchantInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.ENCHANT_TABLE, 14, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_ENCHANT;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        if (this.getViewers().size() == 0) {
            for (int i = 0; i < 2; ++i) {
                who.getInventory().addItem(this.getItem(i));
                this.clear(i);
            }
        }
    }
}
