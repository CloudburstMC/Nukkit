package cn.nukkit.item;

public class ItemChestBoatCherry extends ItemChestBoat {

    public ItemChestBoatCherry() {
        this(0, 1);
    }

    public ItemChestBoatCherry(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatCherry(Integer meta, int count) {
        super(CHERRY_CHEST_BOAT, meta, count, "Cherry Boat with Chest");
    }

    @Override
    public int getVariant() {
        return 8;
    }
}
