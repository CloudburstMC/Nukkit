package cn.nukkit.server.item;

public class ItemIronHorseArmor extends Item {
    public ItemIronHorseArmor() {
        this(0, 0);
    }

    public ItemIronHorseArmor(Integer meta) {
        this(meta, 0);
    }

    public ItemIronHorseArmor(Integer meta, int count) {
        super(IRON_HORSE_ARMOR, meta, count, "Iron horse armor");
    }
}
