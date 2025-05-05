package cn.nukkit.item;

public class ItemChestBoatOak extends ItemChestBoat {

    public ItemChestBoatOak() {
        this(0, 1);
    }

    public ItemChestBoatOak(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatOak(Integer meta, int count) {
        super(OAK_CHEST_BOAT, meta, count, "Oak Boat with Chest");
    }

    @Override
    public int getVariant() {
        return 0;
    }
}
