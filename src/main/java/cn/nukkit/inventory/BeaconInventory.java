package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.level.Position;

/**
 * author: Rover656
 */
public class BeaconInventory extends FakeBlockUIComponent {

    public BeaconInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.BEACON, 27, position);
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        //Drop item in slot
        this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), this.getItem(0));
        this.clear(0);
    }
}
