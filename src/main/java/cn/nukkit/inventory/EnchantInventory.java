package cn.nukkit.inventory;

import cn.nukkit.block.Block;
import cn.nukkit.player.Player;
import cn.nukkit.player.Player.CraftingType;


/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantInventory extends FakeBlockUIComponent {

    public EnchantInventory(PlayerUIInventory playerUI, Block block) {
        super(playerUI, InventoryType.ENCHANT_TABLE, 14, block);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = CraftingType.ENCHANT;
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
