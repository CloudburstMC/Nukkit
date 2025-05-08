package cn.nukkit.item;

public class ItemBrush extends Item {

    public ItemBrush() {
        this(0, 1);
    }

    public ItemBrush(Integer meta) {
        this(meta, 1);
    }

    public ItemBrush(Integer meta, int count) {
        super(BRUSH, meta, count, "Brush");
    }
}
