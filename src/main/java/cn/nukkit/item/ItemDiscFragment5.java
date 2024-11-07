package cn.nukkit.item;

public class ItemDiscFragment5 extends Item {

    public ItemDiscFragment5() {
        this(0, 1);
    }

    public ItemDiscFragment5(Integer meta) {
        this(meta, 1);
    }

    public ItemDiscFragment5(Integer meta, int count) {
        super(DISC_FRAGMENT_5, meta, count, "Disc Fragment");
    }
}
