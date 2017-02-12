package cn.nukkit.item;

public class ItemEnderPearl extends Item {

    public ItemEnderPearl() {
        this(0, 1);
    }

    public ItemEnderPearl(Integer meta) {
        this(meta, 1);
    }

    public ItemEnderPearl(Integer meta, int count) {
        super(ENDER_PEARL, 0, count, "Ender Pearl");
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
