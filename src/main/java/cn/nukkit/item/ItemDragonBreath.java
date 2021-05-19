package cn.nukkit.item;

public class ItemDragonBreath extends Item {

    public ItemDragonBreath() {
        this(0, 1);
    }

    public ItemDragonBreath(Integer meta) {
        this(meta, 1);
    }

    public ItemDragonBreath(Integer meta, int count) {
        super(DRAGON_BREATH, meta, count, "Dragon's Breath");
    }
}
