package cn.nukkit.item;

public class ItemChestBoatSpruce extends ItemChestBoat {

    public ItemChestBoatSpruce() {
        this(0, 1);
    }

    public ItemChestBoatSpruce(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatSpruce(Integer meta, int count) {
        super(SPRUCE_CHEST_BOAT, meta, count, "Spruce Boat with Chest");
    }

    @Override
    public int getVariant() {
        return 1;
    }
}
