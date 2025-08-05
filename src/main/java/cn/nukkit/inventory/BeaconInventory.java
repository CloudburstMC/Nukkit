package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Position;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author Rover656
 */
public class BeaconInventory extends FakeBlockUIComponent {


    /**
     * Items that can be put into beacon inventory
     */
    public static final IntSet ITEMS = new IntOpenHashSet(new int[]{Item.AIR, ItemID.NETHERITE_INGOT, ItemID.EMERALD, ItemID.DIAMOND, ItemID.GOLD_INGOT, ItemID. IRON_INGOT});

    public BeaconInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.BEACON, 27, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.BEACON_WINDOW_ID;
    }

    @Override
    public boolean allowedToAdd(Item item) {
        return ITEMS.contains(item.getId());
    }
}
