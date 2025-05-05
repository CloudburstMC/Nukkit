package cn.nukkit.item;

public class ItemChestBoatDarkOak extends ItemChestBoat {

    public ItemChestBoatDarkOak() {
        this(0, 1);
    }

    public ItemChestBoatDarkOak(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatDarkOak(Integer meta, int count) {
        super(DARK_OAK_CHEST_BOAT, meta, count, "Dark Oak Boat with Chest");
    }

    @Override
    public int getVariant() {
        return 5;
    }
}
