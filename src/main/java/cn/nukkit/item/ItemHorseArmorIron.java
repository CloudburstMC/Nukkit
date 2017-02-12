package cn.nukkit.item;

public class ItemHorseArmorIron extends Item {
    public ItemHorseArmorIron() {
        this(0, 0);
    }

    public ItemHorseArmorIron(Integer meta) {
        this(meta, 0);
    }

    public ItemHorseArmorIron(Integer meta, int count) {
        super(IRON_HORSE_ARMOR, meta, count, "Iron Horse Armor");
    }
}
