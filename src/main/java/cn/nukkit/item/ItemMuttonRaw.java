package cn.nukkit.item;

public class ItemMuttonRaw extends ItemEdible {

    public ItemMuttonRaw() {
        this(0, 1);
    }

    public ItemMuttonRaw(Integer meta) {
        this(meta, 1);
    }

    public ItemMuttonRaw(Integer meta, int count) {
        super(RAW_MUTTON, meta, count, "Raw Mutton");
    }
}
