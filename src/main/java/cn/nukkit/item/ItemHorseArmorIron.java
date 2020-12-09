package cn.nukkit.item;

public class ItemHorseArmorIron extends Item {
    public ItemHorseArmorIron() {
        this(0, 1);
    }

    public ItemHorseArmorIron(Integer meta) {
        this(meta, 1);
    }

    public ItemHorseArmorIron(Integer meta, int count) {
        super(IRON_HORSE_ARMOR, meta, count, "Iron Horse Armor");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
