package cn.nukkit.item;

public class ItemLeatherHorseArmor extends Item {
    public ItemLeatherHorseArmor() {
        this(0, 1);
    }

    public ItemLeatherHorseArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemLeatherHorseArmor(Integer meta, int count) {
        super(LEATHER_HORSE_ARMOR, meta, count, "Leather horse armor");
    }
}
