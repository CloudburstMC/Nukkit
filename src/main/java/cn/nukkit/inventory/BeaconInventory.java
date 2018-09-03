package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.level.Position;

/**
 * author: Rover656
 */
public class BeaconInventory extends ContainerInventory {

    @Override
    public int getSize() {
        return 1;
    }

    public BeaconInventory(Position position) {
        super(null, InventoryType.BEACON);
        this.holder = new FakeBlockMenu(this, position);
    }

    @Override
    public FakeBlockMenu getHolder() {
        return (FakeBlockMenu) this.holder;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;
        who.resetCraftingGridType();

        //Drop item in slot
        this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), this.getItem(0));
        this.clear(0);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_ANVIL;
    }


}
