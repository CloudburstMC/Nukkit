package cn.nukkit.inventory;

import cn.nukkit.block.Block;
import cn.nukkit.player.Player;

/**
 * author: Rover656
 */
public class BeaconInventory extends FakeBlockUIComponent {

    public BeaconInventory(PlayerUIInventory playerUI, Block block) {
        super(playerUI, InventoryType.BEACON, 27, block);
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        //Drop item in slot
        this.getHolder().getLevel().dropItem(this.getHolder().getPosition(), this.getItem(0));
        this.clear(0);
    }
}
