package cn.nukkit.item;

public class ItemChestBoatPaleOak extends ItemChestBoat {

    public ItemChestBoatPaleOak() {
        this(0, 1);
    }

    public ItemChestBoatPaleOak(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatPaleOak(Integer meta, int count) {
        super(PALE_OAK_CHEST_BOAT, meta, count, "Pale Oak Boat with Chest");
    }

    @Override
    public int getVariant() {
        return 9;
    }
}
