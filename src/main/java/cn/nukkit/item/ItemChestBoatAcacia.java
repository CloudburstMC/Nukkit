package cn.nukkit.item;

public class ItemChestBoatAcacia extends ItemChestBoat {

    public ItemChestBoatAcacia() {
        this(0, 1);
    }

    public ItemChestBoatAcacia(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatAcacia(Integer meta, int count) {
        super(ACACIA_CHEST_BOAT, meta, count, "Acacia Boat with Chest");
    }

    @Override
    public int getVariant() {
        return 4;
    }
}
