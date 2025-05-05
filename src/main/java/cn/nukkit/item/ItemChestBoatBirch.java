package cn.nukkit.item;

public class ItemChestBoatBirch extends ItemChestBoat {

    public ItemChestBoatBirch() {
        this(0, 1);
    }

    public ItemChestBoatBirch(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatBirch(Integer meta, int count) {
        super(BIRCH_CHEST_BOAT, meta, count, "Birch Boat with Chest");
    }

    @Override
    public int getVariant() {
        return 2;
    }
}
