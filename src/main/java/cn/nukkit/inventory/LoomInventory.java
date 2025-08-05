package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Position;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class LoomInventory extends FakeBlockUIComponent {

    public static final int OFFSET = 9;

    public static final IntSet ITEMS = new IntOpenHashSet(new int[]{Item.AIR, ItemID.BANNER, ItemID.DYE, ItemID.BANNER_PATTERN});

    public LoomInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.LOOM, OFFSET, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.LOOM_WINDOW_ID;
    }

    public Item getBanner() {
        return getItem(0);
    }

    public Item getDye() {
        return getItem(1);
    }

    public Item getPattern() {
        return getItem(2);
    }

    public void setBanner(Item item) {
        this.setItem(0, item);
    }

    public void setDye(Item item) {
        this.setItem(1, item);
    }

    public void setPattern(Item item) {
        this.setItem(2, item);
    }

    @Override
    public boolean allowedToAdd(Item item) {
        return ITEMS.contains(item.getId());
    }
}
