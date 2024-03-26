package cn.nukkit.item;

public class ItemIronRaw extends Item {

    public ItemIronRaw() {
        this(0, 1);
    }

    public ItemIronRaw(Integer meta) {
        this(meta, 1);
    }

    public ItemIronRaw(Integer meta, int count) {
        super(RAW_IRON, meta, count, "Raw Iron");
    }
}
