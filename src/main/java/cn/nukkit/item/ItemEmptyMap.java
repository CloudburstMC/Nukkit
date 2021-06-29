package cn.nukkit.item;

public class ItemEmptyMap extends Item {

    public ItemEmptyMap() {
        this(0, 1);
    }

    public ItemEmptyMap(Integer meta) {
        this(meta, 1);
    }

    public ItemEmptyMap(Integer meta, int count) {
        super(EMPTY_MAP, meta, count, "Empty Map");
    }
}
