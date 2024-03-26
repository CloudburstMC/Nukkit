package cn.nukkit.item;

public class ItemGoldRaw extends Item {

    public ItemGoldRaw() {
        this(0, 1);
    }

    public ItemGoldRaw(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldRaw(Integer meta, int count) {
        super(RAW_GOLD, meta, count, "Raw Gold");
    }
}
