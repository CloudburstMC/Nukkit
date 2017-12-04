package cn.nukkit.server.item;

public class ItemHorseArmorDiamond extends Item {
    public ItemHorseArmorDiamond() {
        this(0, 0);
    }

    public ItemHorseArmorDiamond(Integer meta) {
        this(meta, 0);
    }

    public ItemHorseArmorDiamond(Integer meta, int count) {
        super(DIAMOND_HORSE_ARMOR, meta, count, "Diamond Horse Armor");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
