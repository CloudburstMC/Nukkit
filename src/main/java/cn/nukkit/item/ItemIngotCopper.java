package cn.nukkit.item;

public class ItemIngotCopper extends Item {

    public ItemIngotCopper() {
        this(0, 1);
    }

    public ItemIngotCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemIngotCopper(Integer meta, int count) {
        super(COPPER_INGOT, 0, count, "Copper Ingot");
    }
}
