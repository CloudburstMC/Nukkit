package cn.nukkit.item;

public class ItemChestBoatMangrove extends ItemChestBoat {

    public ItemChestBoatMangrove() {
        this(0, 1);
    }

    public ItemChestBoatMangrove(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatMangrove(Integer meta, int count) {
        super(MANGROVE_CHEST_BOAT, meta, count, "Mangrove Boat with Chest");
    }

    @Override
    public int getVariant() {
        return 6;
    }
}
