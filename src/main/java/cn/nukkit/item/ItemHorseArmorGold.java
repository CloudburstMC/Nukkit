package cn.nukkit.item;

public class ItemHorseArmorGold extends Item {
    public ItemHorseArmorGold() {
        this(0, 0);
    }

    public ItemHorseArmorGold(Integer meta) {
        this(meta, 0);
    }

    public ItemHorseArmorGold(Integer meta, int count) {
        super(GOLD_HORSE_ARMOR, meta, count, "Gold Horse Armor");
    }

}
