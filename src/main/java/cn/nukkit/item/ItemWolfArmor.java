package cn.nukkit.item;

public class ItemWolfArmor extends Item {

    public ItemWolfArmor() {
        this(0, 1);
    }

    public ItemWolfArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemWolfArmor(Integer meta, int count) {
        super(WOLF_ARMOR, meta, count, "Wolf Armor");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
