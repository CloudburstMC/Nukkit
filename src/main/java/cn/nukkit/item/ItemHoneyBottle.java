package cn.nukkit.item;

public class ItemHoneyBottle extends Item {

    public ItemHoneyBottle() {
        this(0, 1);
    }

    public ItemHoneyBottle(Integer meta) {
        this(meta, 1);
    }

    public ItemHoneyBottle(Integer meta, int count) {
        super(ItemID.HONEY_BOTTLE, meta, count, "Honey Bottle");
    }
}
