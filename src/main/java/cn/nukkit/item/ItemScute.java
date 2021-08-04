package cn.nukkit.item;

public class ItemScute extends Item {

    public ItemScute() {
        this(0, 1);
    }

    public ItemScute(Integer meta) {
        this(meta, 1);
    }

    public ItemScute(Integer meta, int count) {
        super(SCUTE, meta, count, "Scute");
    }
}
