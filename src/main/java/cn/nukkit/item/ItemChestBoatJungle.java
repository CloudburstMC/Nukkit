package cn.nukkit.item;

public class ItemChestBoatJungle extends ItemChestBoat {

    public ItemChestBoatJungle() {
        this(0, 1);
    }

    public ItemChestBoatJungle(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatJungle(Integer meta, int count) {
        super(JUNGLE_CHEST_BOAT, meta, count, "Jungle Boat with Chest");
    }

    @Override
    public int getVariant() {
        return 3;
    }
}
