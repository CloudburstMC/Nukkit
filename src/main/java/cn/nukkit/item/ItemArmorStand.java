package cn.nukkit.item;

public class ItemArmorStand extends Item {

    public ItemArmorStand() {
        this(0);
    }

    public ItemArmorStand(Integer meta) {
        this(meta, 1);
    }

    public ItemArmorStand(Integer meta, int count) {
        super(ARMOR_STAND, meta, count, "Armor Stand");
    }
}
