package cn.nukkit.item;

public class ItemDiamondHorseArmor extends Item {
    public ItemDiamondHorseArmor() {
        this(0, 0);
    }

    public ItemDiamondHorseArmor(Integer meta) {
        this(meta, 0);
    }

    public ItemDiamondHorseArmor(Integer meta, int count) {
        super(GOLD_HORSE_ARMOR, meta, count, "Diamond horse armor");
    }
}
