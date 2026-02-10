package cn.nukkit.item;

public class ItemOminousBottle extends Item {

    public ItemOminousBottle() {
        this(0, 1);
    }

    public ItemOminousBottle(Integer meta) {
        this(meta, 1);
    }

    public ItemOminousBottle(Integer meta, int count) {
        super(OMINOUS_BOTTLE, meta, count, "Ominous Bottle");
    }
}
