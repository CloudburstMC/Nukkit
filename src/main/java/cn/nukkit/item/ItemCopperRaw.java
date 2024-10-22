package cn.nukkit.item;

public class ItemCopperRaw extends Item {

    public ItemCopperRaw() {
        this(0, 1);
    }

    public ItemCopperRaw(Integer meta) {
        this(meta, 1);
    }

    public ItemCopperRaw(Integer meta, int count) {
        super(RAW_COPPER, meta, count, "Raw Copper");
    }
}
