package cn.nukkit.item;

public class ItemBreezeRod extends Item {

    public ItemBreezeRod() {
        this(0, 1);
    }

    public ItemBreezeRod(Integer meta) {
        this(meta, 1);
    }

    public ItemBreezeRod(Integer meta, int count) {
        super(BREEZE_ROD, meta, count, "Breeze Rod");
    }
}
